package net.danygames2014.nyaview;

import net.danygames2014.nyaview.gui.MappingGui;
import net.danygames2014.nyaview.gui.TestGui;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Util;
import net.fabricmc.mappingio.format.MappingFormat;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NyaView {
    public static void main(String[] args) throws IOException {
        MappingLoader loader = new MappingLoader();

        loader.addMappings(new Mappings("intermediary", "Intermediary", MappingType.INTERMEDIARY, Util.getMappingPath("intermediary.tiny"), MappingFormat.TINY_2_FILE));
        loader.addMappings(new Mappings("mcp_client", "MCP Client", MappingType.MCP_CLIENT, Util.getMappingPath("mcp-client.tiny"), MappingFormat.TINY_2_FILE));
        loader.addMappings(new Mappings("mcp_server", "MCP Server", MappingType.MCP_SERVER, Util.getMappingPath("mcp-server.tiny"), MappingFormat.TINY_2_FILE));
        loader.addMappings(new Mappings("biny", "BINY", MappingType.BABRIC, Util.getMappingPath("biny"), MappingFormat.ENIGMA_DIR));
        loader.addMappings(new Mappings("bin", "BIN", MappingType.BABRIC, Util.getMappingPath("bin"), MappingFormat.ENIGMA_DIR));
        loader.addMappings(new Mappings("bin-bh", "BIN-BH", MappingType.BABRIC, Util.getMappingPath("bin-bh"), MappingFormat.ENIGMA_DIR));
        loader.addMappings(new Mappings("nya", "NYA", MappingType.BABRIC, Util.getMappingPath("nya"), MappingFormat.ENIGMA_DIR));

        MappingGui mappingGui = new MappingGui(loader);
        mappingGui.refreshTable();

//        TestGui testGui = new TestGui();






//        VisitableMappingTree mappings = new MemoryMappingTree();
//        MappingReader.read(Paths.get(System.getProperty("user.dir") + "/mappings/" + "intermediary.tiny"), MappingFormat.TINY_2_FILE, mappings);

//        for (var item : mappings.getClasses()){
//            String[] srcname = item.getSrcName().split("/");
//            if(srcname[0].equals("argo")){
//                continue;
//            }
//            System.out.println(srcname[srcname.length-1] + " -> " + item.getName("client") + " | " + item.getName("server"));
//        }

//        for (var item : mappings.getDstNamespaces()){
//            System.out.println(item);
//        }

//        for (var item : biny.getClasses()) {
//            System.out.println(item.getSrcName() + " -> " + item.getName(0));
//            System.out.println("Methods :");
//            for (var method : item.getMethods()) {
//                System.out.print(method.getSrcName() + " -> " + method.getName("target") + " ");
//                System.out.println("DESCRIPTOR : " + method.getDesc(0));
//                Descriptor descriptor = DescriptorParser.parseDescriptor(method.getDesc(0));

//                for (var arg : method.getArgs()){
//                    System.out.println(arg.getName(0));
//                }

//                System.out.println();

//                for (var argType : descriptor.args){
//                    System.out.println(argType);
//                }

//                System.in.read();
//                for (var arg : method.getArgs()){
//                    System.out.print(arg.getName("client") + " ");
//                }
//                System.out.println(")");
//            }
//            System.out.println("Fields :");
//            for (var field : item.getFields()) {
//                System.out.println("- " + field.getSrcName() + " -> " + field.getName("client"));
//            }
//        }

//        Mappings mappings = new Mappings();
//        mappings.addMappings("intermediary", "intermediary.tiny", MappingFormat.TINY_2_FILE);
//        net.danygames2014.nyaview.mapping.addMappings("mcp-client", "mcp-client.tiny", MappingFormat.TINY_2_FILE);
//        mappings.addMappings("biny", "biny/", MappingFormat.ENIGMA_DIR);
//        mappings.addMappings("bin", "bin/", MappingFormat.ENIGMA_DIR);
//        mappings.addMappings("bin-bh", "bin-bh", MappingFormat.ENIGMA_DIR);
//        mappings.addMappings("nya", "nya", MappingFormat.ENIGMA_DIR);


//        public void addMappings(String mappingName, String mappingPath, MappingFormat mappingFormat) throws IOException {
//            VisitableMappingTree tree = new MemoryMappingTree();
//            MappingReader.read(Paths.get(System.getProperty("user.dir") + "/mappings/" + mappingPath), mappingFormat, tree);
//            mappingTrees.put(mappingName, tree);
//        }
    }
}
