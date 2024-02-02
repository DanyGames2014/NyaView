package net.danygames2014.nyaview.gui;

import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class MappingGui extends JFrame {
    public JPanel mainPanel;
    public JPanel toolbarPanel;
    public BorderLayout mainLayout;
    public FlowLayout toolbarLayout;

    public JTextField searchField;
    public JButton searchButton;

    public JSplitPane mainSplitPane;
    public JSplitPane methodFieldSplitPane;

    public JTable classTable;
    public DefaultTableModel classTableModel;
    public MappingLoader loader;

    public MappingGui(MappingLoader loader) throws HeadlessException {
        // Set Title and close operation
        super("NyaView");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Boring Constructor Stuff
        this.loader = loader;

        // Initialize window elements
        initialize();

        // Set the size and make the window visible
        this.setSize(1280,720);
        this.setVisible(true);

        // Refresh the tables
        refreshTable();

    }

    public void initialize(){
        // Create Layouts
        mainLayout = new BorderLayout();
        toolbarLayout = new FlowLayout(FlowLayout.LEFT);

        // Create main panel and set layout
        mainPanel = new JPanel(mainLayout);

        // Create toolbar panel and set layout
        toolbarPanel = new JPanel(toolbarLayout);

        // Create Toolbar Items
        searchField = new JTextField("", 40);
        searchButton = new JButton("Search");

        // Add toolbar items to toolbar panel
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);

        // Add toolbar panel to main panel
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Create the main split pane
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerSize(5);

        // Create Table for Class Mapping Entries
        classTable = new JTable(classTableModel);
        classTable.setBounds(0,0,1280,720);
        classTable.setAutoCreateRowSorter(true);
        classTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        classTable.setRowSelectionAllowed(false);
        classTable.setColumnSelectionAllowed(false);
        classTable.setDefaultEditor(Object.class, null);

        // Create a new Scroll Pane with the Class Table and add it to the main split pane
        JScrollPane scrollPane = new JScrollPane(classTable);
        mainSplitPane.add(scrollPane);

        // Create a method and field split pane and add it to the main split pane
        methodFieldSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        methodFieldSplitPane.setDividerSize(5);
        mainSplitPane.add(methodFieldSplitPane);

        // Method Panel
        JPanel methodPanel = new JPanel();
        methodPanel.setBackground(Color.CYAN);
        methodPanel.setPreferredSize(new Dimension(600,200));
        methodFieldSplitPane.add(methodPanel);

        // Field Panel
        JPanel fieldPanel = new JPanel();
        fieldPanel.setBackground(Color.PINK);
        methodFieldSplitPane.add(fieldPanel);

        // Add split pane to main panel
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        // Add Main Panel to JFrame
        this.add(mainPanel);
    }

    public void refreshTable(){
        System.out.println("Refreshing Table");

        classTableModel = new DefaultTableModel();

        // Default column names which are not changing
        String[] defaultColumnNames = {"Environment", "MCP", "Obfuscated Client", "Obfuscated Server", "Intermediary"};

        // Create column names
        for(var columnName : defaultColumnNames){
            classTableModel.addColumn(columnName);
        }

        // Add Babric mapping columns
        for (var mapping : loader.mappings.values()){
            if(mapping.type == MappingType.BABRIC){
                classTableModel.addColumn(mapping.visibleName);
            }
        }

        // Add Rows
        for (var classEntry : loader.classes.values()){
            ArrayList<String> row = new ArrayList<>(7);
            row.add(classEntry.environment.toString());
            row.add(classEntry.mcp);
            row.add(classEntry.obfuscatedClient);
            row.add(classEntry.obfuscatedServer);
            row.add(classEntry.intermediary);

            for (Mappings babricMapping : loader.mappings.values()){
                if(babricMapping.type == MappingType.BABRIC){
                    row.add(classEntry.getBabricName(babricMapping));
                }
            }

            classTableModel.addRow(row.toArray());
        }

        classTable.setModel(classTableModel);
    }
}
