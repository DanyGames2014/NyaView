package net.danygames2014.nyaview.mapping;

import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.mapping.entry.FieldMappingEntry;
import net.danygames2014.nyaview.mapping.entry.MethodMappingEntry;
import net.danygames2014.nyaview.util.Environment;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.mappingio.tree.VisitableMappingTree;

import java.io.IOException;
import java.util.HashMap;

public class MappingLoader {
    public HashMap<String, Mappings> mappings;
    public HashMap<String, ClassMappingEntry> classes;

    public MappingLoader() {
        this.classes = new HashMap<>();
        this.mappings = new HashMap<>();
    }

    public void addMappings(Mappings mappingSet) {
        if (!mappings.containsValue(mappingSet)) {
            switch (mappingSet.type) {
                case INTERMEDIARY -> {
                    loadIntermediary(mappingSet);
                }

                case MCP_CLIENT -> {
                    loadMcp(mappingSet, Environment.CLIENT);
                }

                case MCP_SERVER -> {
                    loadMcp(mappingSet, Environment.SERVER);

                }

                case BABRIC -> {
                    loadBabric(mappingSet);
                }
            }
            mappings.put(mappingSet.name, mappingSet);
        } else {
//            NyaView.LOGGER.warning("Mappings " + mappingSet.name + " already exist.");
        }
    }

    public void loadIntermediary(Mappings mappingSet) {
        VisitableMappingTree intermediary = new MemoryMappingTree();
        try {
            MappingReader.read(mappingSet.path, mappingSet.format, intermediary);

            for (var item : intermediary.getClasses()) {

                String[] srcname = item.getSrcName().split("/");

                // Ignore argo library
                if (srcname[0].equals("argo")) {
                    continue;
                }

                // Split Source name into package and name
                StringBuilder pkg = new StringBuilder(srcname.length - 1);
                for (int i = 0; i < srcname.length - 1; i++) {
                    pkg.append(srcname[i]);

                    if (i < (srcname.length - 2)) {
                        pkg.append(".");
                    }
                }

                // Create the Class Mapping Entry
                ClassMappingEntry classEntry = new ClassMappingEntry();

                // Determine Side
                if (item.getSrcName().equals(item.getName("server"))) {
                    classEntry.environment = Environment.CLIENT;
                } else if (item.getSrcName().equals(item.getName("client"))) {
                    classEntry.environment = Environment.SERVER;
                } else {
                    classEntry.environment = Environment.MERGED;
                }

                // Intermediary
                classEntry.intermediary = srcname[srcname.length - 1];
                classEntry.intermediaryPackage = pkg.toString();

                // Obfuscated
                classEntry.obfuscatedClient = item.getName("client");
                classEntry.obfuscatedServer = item.getName("server");

                if (classEntry.environment == Environment.SERVER) {
                    classEntry.obfuscatedClient = "";
                }

                if (classEntry.environment == Environment.CLIENT) {
                    classEntry.obfuscatedServer = "";
                }

                // Methods
                for (MappingTree.MethodMapping method : item.getMethods()) {
                    MethodMappingEntry methodEntry = new MethodMappingEntry();

                    methodEntry.intermediary = method.getSrcName();

                    // If the client and intermediary names are the same, it doesnt exist on client
                    if (method.getName("client").equals(methodEntry.intermediary)) {
                        methodEntry.obfuscatedClient = "";
                    } else {
                        methodEntry.obfuscatedClient = method.getName("client");
                        System.out.println(method.getDesc("client"));
                    }

                    // If the server and intermnediary names are the same, it doesnt exist on the server
                    if (method.getName("server").equals(methodEntry.intermediary)) {
                        methodEntry.obfuscatedServer = "";
                    } else {
                        methodEntry.obfuscatedServer = method.getName("server");
                    }

                    classEntry.methods.put(methodEntry.intermediary, methodEntry);
                }

                // Fields
                for (MappingTree.FieldMapping field : item.getFields()) {
                    var fieldEntry = new FieldMappingEntry();

                    fieldEntry.intermediary = field.getSrcName();

                    if (field.getName("client").equals(fieldEntry.intermediary)) {
                        fieldEntry.obfuscatedClient = "";
                    } else {
                        fieldEntry.obfuscatedClient = field.getName("client");
                    }

                    if (field.getName("server").equals(fieldEntry.intermediary)) {
                        fieldEntry.obfuscatedServer = "";
                    } else {
                        fieldEntry.obfuscatedServer = field.getName("server");
                    }
                    classEntry.fields.put(fieldEntry.intermediary, fieldEntry);
                }

                classes.put(classEntry.intermediary, classEntry);

            }

        } catch (IOException e) {
            System.out.println("Failed to load intermediary, file not found");
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }

        System.out.println("Intermediary loaded");

    }

    public void loadMcp(Mappings mappingSet, Environment environment) {
        VisitableMappingTree mcp = new MemoryMappingTree();
        try {
            MappingReader.read(mappingSet.path, mappingSet.format, mcp);

            for (var item : mcp.getClasses()) {

                String obfuscatedName = item.getSrcName();

                for (var classEntry : classes.values()) {
                    if (environment == Environment.CLIENT) {
                        if (classEntry.obfuscatedClient.equals(obfuscatedName)) {
                            if (classEntry.environment == Environment.SERVER) {
                                throw new IllegalStateException("Found a matching obfuscated name for client but the environment is server");
                            }
                            String[] mcpName = item.getName(0).split("/");
                            classEntry.mcp = mcpName[mcpName.length - 1];

                            System.out.println("CLASS " + classEntry.mcp);
                            for (var method : item.getMethods()) {
//                                System.out.println(method);
                                for (var class_method : classEntry.methods.values()) {
//                                    System.out.println("?" + class_method.obfuscatedClient + " -> " + method.getSrcName());
                                    if(class_method.obfuscatedClient.equals(method.getSrcName())){
                                        System.out.println(class_method.obfuscatedClient + " -> " + method.getName(0));
                                        System.out.println(method.getSrcDesc());
                                    }
                                }
                            }
                            System.out.println();
                        }

                    } else if (environment == Environment.SERVER) {
                        if (classEntry.obfuscatedServer.equals(obfuscatedName)) {
                            String[] mcpName = item.getName(0).split("/");
                            // Use Server mapping only if client one isn't present
                            if (classEntry.mcp == null && classEntry.environment == Environment.SERVER) {
                                classEntry.mcp = mcpName[mcpName.length - 1];
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Failed to load MCP " + environment);
            e.printStackTrace();
        }

        System.out.println("MCP " + environment.toString() + " loaded");
    }

    public void loadBabric(Mappings mappingSet) {
        VisitableMappingTree babric = new MemoryMappingTree();
        try {
            MappingReader.read(mappingSet.path, mappingSet.format, babric);

            for (var item : babric.getClasses()) {

                String obfuscatedName = item.getSrcName();

                String[] srcname = item.getSrcName().split("/");
                String className = srcname[srcname.length - 1];

                if (classes.containsKey(className)) {
                    classes.get(className).babric.put(mappingSet, item.getName("target"));
                }

                // Methods
//                for (MappingTree.MethodMapping method : item.getMethods()){
//                    System.out.println("Class : " + item.getName("target") + "." + method.getName("target"));
//                }
            }

        } catch (IOException e) {
            System.out.println("Failed to load intermediary, file not found");
            e.printStackTrace();
        }

        System.out.println(mappingSet.visibleName + " loaded");
    }
}
