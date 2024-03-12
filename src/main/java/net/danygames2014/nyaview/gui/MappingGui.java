package net.danygames2014.nyaview.gui;

import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.mapping.entry.MethodMappingEntry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MappingGui extends JFrame {
    // Main Layout
    public JPanel mainPanel;
    public BorderLayout mainLayout;

    // Toolbar
    public JPanel toolbarPanel;
    public FlowLayout toolbarLayout;

    public JTextField searchField;
    public JButton searchButton;

    public JButton reloadButton;

    // Split panes
    public JSplitPane mainSplitPane;
    public JSplitPane methodFieldSplitPane;

    // Class Table
    public JTable classTable;
    public DefaultTableModel classTableModel;

    // Method Table
    public JTable methodTable;
    public DefaultTableModel methodTableModel;

    // Field Table
    public JTable fieldTable;
    public DefaultTableModel fieldTableModel;

    // Loader
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
        this.setSize(1280, 720);
        this.setVisible(true);

        // Refresh Class Table
        refreshTables();
    }

    public void initialize() {
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

        reloadButton = new JButton("Reload");

        // Add toolbar items to toolbar panel
        toolbarPanel.add(searchField);
        toolbarPanel.add(searchButton);
        toolbarPanel.add(reloadButton);

        // Add toolbar panel to main panel
        mainPanel.add(toolbarPanel, BorderLayout.NORTH);

        // Create the main split pane
        mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setDividerSize(5);

        // Create Table for Class Mapping Entries
        classTable = new JTable(classTableModel);
//        classTable.setBounds(0, 0, 1280, 720);
        classTable.setAutoCreateRowSorter(true);
        classTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        classTable.setRowSelectionAllowed(false);
        classTable.setColumnSelectionAllowed(false);
        classTable.setDefaultEditor(Object.class, null);
        classTable.getSelectionModel().addListSelectionListener(new ClassTableListener(classTable));

        // Create a new Scroll Pane with the Class Table and add it to the main split pane
        JScrollPane classScrollPane = new JScrollPane(classTable);
        mainSplitPane.add(classScrollPane);

        // Create a method and field split pane and add it to the main split pane
        methodFieldSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        methodFieldSplitPane.setDividerSize(5);
        mainSplitPane.add(methodFieldSplitPane);

        // Create Methods Table
        methodTable = new JTable(methodTableModel);
        methodTable.setBackground(Color.CYAN);
        methodTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        methodTable.setAutoCreateRowSorter(true);
        methodTable.setRowSelectionAllowed(false);
        methodTable.setColumnSelectionAllowed(false);
        methodTable.setDefaultEditor(Object.class, null);
        JScrollPane methodScrollPane = new JScrollPane(methodTable);
        methodFieldSplitPane.add(methodScrollPane);

        // Create Field Table
        fieldTable = new JTable(fieldTableModel);
        fieldTable.setBackground(Color.PINK);
        fieldTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        fieldTable.setAutoCreateRowSorter(true);
        fieldTable.setRowSelectionAllowed(false);
        fieldTable.setColumnSelectionAllowed(false);
        fieldTable.setDefaultEditor(Object.class, null);
        JScrollPane fieldScrollPane = new JScrollPane(fieldTable);
        methodFieldSplitPane.add(fieldScrollPane);

        // Set Panel Resize Weights
        mainSplitPane.setResizeWeight(0.6d);
        methodFieldSplitPane.setResizeWeight(0.4d);

        // Add split pane to main panel
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        // Add Main Panel to JFrame
        this.add(mainPanel);
    }

    public void refreshTables() {
        /// Model Table
        System.out.println("Refreshing Model Table");

        methodTableModel = initTableModel();

        methodTable.setModel(methodTableModel);

        /// Field Table
        System.out.println("Refreshing Field Table");

        fieldTableModel = initTableModel();

        fieldTable.setModel(fieldTableModel);

        /// Class Table
        System.out.println("Refreshing Class Table");

        classTableModel = initTableModel();

        // Add Rows
        for (var classEntry : loader.classes.values()) {
            ArrayList<String> row = new ArrayList<>(7);
            row.add(classEntry.environment.toString());
            row.add(classEntry.mcp);
            row.add(classEntry.obfuscatedClient);
            row.add(classEntry.obfuscatedServer);
            row.add(classEntry.intermediary);

            for (Mappings babricMapping : loader.mappings.values()) {
                if (babricMapping.type == MappingType.BABRIC) {
                    row.add(classEntry.getBabricName(babricMapping));
                }
            }

            classTableModel.addRow(row.toArray());
//            methodTableModel.addRow(row.toArray());
        }

        classTable.setModel(classTableModel);
    }

    public DefaultTableModel initTableModel(){
        DefaultTableModel tableModel = new DefaultTableModel();

        // Default column names which are not changing
        String[] defaultColumnNames = {"Environment", "MCP", "Obfuscated Client", "Obfuscated Server", "Intermediary"};

        // Create column names
        for (var columnName : defaultColumnNames) {
            tableModel.addColumn(columnName);
        }

        // Add Babric mapping columns
        for (var mapping : loader.mappings.values()) {
            if (mapping.type == MappingType.BABRIC) {
                tableModel.addColumn(mapping.visibleName);
            }
        }

        return tableModel;
    }

    class ClassTableListener implements ListSelectionListener {
        private final JTable table;

        public ClassTableListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int i = table.getSelectedRow();
                if (i > -1) {
                    String intermediary = (String) table.getModel().getValueAt(table.convertRowIndexToModel(i), 4);

                    searchField.setText(intermediary);
                    System.out.println(intermediary);

                    ClassMappingEntry c = loader.classes.get(intermediary);

                    for (MethodMappingEntry method : c.methods.values()) {
                        System.out.println(method.intermediary);
                    }
//                    tblMethods.setModel(currentLoader.getMethodModel(pkg + "/" + name));
//                    tblMethods.setEnabled(true);
//                    tblFields.setModel(currentLoader.getFieldModel(pkg + "/" + name));
//                    tblFields.setEnabled(true);
                } else {
//                    tblMethods.setModel(methodsDefaultModel);
//                    tblMethods.setEnabled(false);
//                    tblFields.setModel(fieldsDefaultModel);
//                    tblFields.setEnabled(false);
                }
            }
        }
    }
}
