package net.danygames2014.nyaview.mapping;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.entry.*;
import net.danygames2014.nyaview.util.Environment;
import net.danygames2014.nyaview.util.Util;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.mappingio.tree.VisitableMappingTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static net.danygames2014.nyaview.util.Logger.*;
import static net.danygames2014.nyaview.util.Util.inputOrEmpty;

@SuppressWarnings("CallToPrintStackTrace")
public class MappingLoader {
    public HashMap<String, Mappings> mappings;
    public ArrayList<ClassMappingEntry> classes;

    public MappingLoader() {
        this.classes = new ArrayList<>();
        this.mappings = new HashMap<>();
    }

    public void load(Mappings mappingSet) {
        debug("Loading " + mappingSet.name);
        if (!mappings.containsValue(mappingSet)) {
            switch (mappingSet.type) {
                case MCP_CLIENT, MCP_SERVER -> {
                    loadMcp(mappingSet);
                }
                case INTERMEDIARY -> {
                    loadIntermediary(mappingSet);
                }
                case BABRIC -> {
                    loadBabric(mappingSet);
                }
            }
            mappings.put(mappingSet.id, mappingSet);
        } else {
            warn("Tried to load mappings that are already loaded: " + mappingSet.name);
        }
    }

    public void loadBabric(Mappings mappingSet) {
        info("Loading " + mappingSet.name + " Babric Mappings");
        VisitableMappingTree babric = new MemoryMappingTree();
        try {
            MappingReader.read(Util.getMappingPath(mappingSet.path), mappingSet.format, babric);
            Mappings intermediary = NyaView.config.getMappings(mappingSet.parentId);
            info("Intermediary " + intermediary.name + " discovered for " + mappingSet.name);

            for (var item : babric.getClasses()) {
                /// Class
                // Generate Class Path
                ClassPath classPath = ClassPath.fromName(item.getSrcName());
                ClassMappingEntry classEntry = findIntermediaryClass(classPath, intermediary);

                if (classEntry == null) {
                    debug("Mappings found for [" + item.getSrcName() + " -> " + item.getDstName(0) + "] but no matching intermediary class was found");
                    continue;
                }

                /// Methods
                for (var method : item.getMethods()) {
                    MethodMappingEntry methodEntry = findIntermediaryMethod(classEntry, intermediary, method.getSrcDesc(), method.getSrcName());

                    if (methodEntry == null) {
                        debug("Mappings found for [" + item.getSrcName() + "." + method.getSrcName() + " -> " + item.getDstName(0) + "." + method.getDstName(0) + "] but no matching intermediary method was found");
                        continue;
                    }

                    Method methodMapping = new Method();
                    // Name
                    methodMapping.name = inputOrEmpty(method.getDstName(0));

                    // Desc
                    methodMapping.desc = method.getDstDesc(0);

                    // Args
                    ArrayList<String> args = new ArrayList<>();
                    for (var argument : method.getArgs()) {
                        args.add(argument.getDstName(0));
                    }
                    methodMapping.args = args;

                    methodEntry.babric.put(mappingSet, methodMapping);
                }

                /// Fields
                for (var field : item.getFields()) {
                    FieldMappingEntry fieldEntry = findIntermediaryField(classEntry, intermediary, field.getSrcName());

                    if (fieldEntry == null) {
                        debug("Mappings found for [" + item.getSrcName() + "." + field.getSrcName() + " -> " + item.getDstName(0) + "." + field.getDstName(0) + "] but no matching intermediary method was found");
                        continue;
                    }

                    fieldEntry.babric.put(mappingSet, inputOrEmpty(field.getDstName(0)));
                }

                String destinationName = item.getDstName(0) != null ? item.getDstName(0) : item.getSrcName();

                classEntry.babric.put(mappingSet, ClassPath.fromName(destinationName));
            }

            info(mappingSet.name + " loaded");
        } catch (IOException e) {
            error("Failed to load " + mappingSet.name + " mappings");
            e.printStackTrace();
        }
    }

    public ClassMappingEntry findIntermediaryClass(ClassPath intermediaryPath, Mappings mappingSet) {
        for (var classEntry : classes) {
            if (classEntry.intermediary.get(mappingSet).equals(intermediaryPath)) {
                return classEntry;
            }
        }
        return null;
    }

    public MethodMappingEntry findIntermediaryMethod(ClassMappingEntry classEntry, Mappings intermediary, String intermediaryDesc, String intermediaryName) {
        for (var method : classEntry.methods) {
            if (method.getIntermediaryName(intermediary).equals(intermediaryName) && method.getIntermediaryDesc(intermediary).equals(intermediaryDesc)) {
                return method;
            }
        }
        return null;
    }

    public FieldMappingEntry findIntermediaryField(ClassMappingEntry classEntry, Mappings intermediary, String intermediaryName) {
        for (var field : classEntry.fields) {
            if (field.getIntermediaryName(intermediary).equals(intermediaryName)) {
                return field;
            }
        }
        return null;
    }

    // MCP
    public void loadMcp(Mappings mappingSet) {
        info("Loading " + mappingSet.name + " MCP Mappings");
        VisitableMappingTree mcp = new MemoryMappingTree();
        try {
            MappingReader.read(Util.getMappingPath(mappingSet.path), mappingSet.format, mcp);

            for (var item : mcp.getClasses()) {
                /// Class
                // Generate Class Path
                ClassPath classPath = ClassPath.fromName(item.getSrcName());

                ClassMappingEntry classEntry;

                switch (mappingSet.type) {
                    case MCP_CLIENT -> {
                        classEntry = findClientClass(item.getSrcName());

                        if (classEntry == null) {
                            debug("Mappings found for [" + item.getSrcName() + " -> " + item.getDstName(0) + "] but no matching intermediary class was found");
                            continue;
                        }

                        if (classEntry.environment == Environment.SERVER) {
                            System.out.println(classEntry);
                            System.out.println("[" + item.getSrcName() + " -> " + item.getDstName(0) + "]");
                            throw new IllegalStateException("Found a matching obfuscated name for client but the environment is server");
                        }

                        /// Methods
                        for (var method : item.getMethods()) {
                            MethodMappingEntry methodEntry = findClientMethod(classEntry, method.getSrcDesc(), method.getSrcName());

                            if (methodEntry == null) {
                                debug("Mappings found for [" + item.getSrcName() + "." + method.getSrcName() + " -> " + item.getDstName(0) + "." + method.getDstName(0) + "] but no matching intermediary method was found");
                                continue;
                            }

                            Method methodMapping = new Method();

                            // Name
                            methodMapping.name = inputOrEmpty(method.getDstName(0));

                            // Desc
                            methodMapping.desc = method.getDstDesc(0);

                            // Args
                            ArrayList<String> args = new ArrayList<>();
                            for (var argument : method.getArgs()) {
                                args.add(argument.getDstName(0));
                            }
                            methodMapping.args = args;

                            methodEntry.mcp.put(mappingSet, methodMapping);
                        }

                        /// Fields
                        for (var field : item.getFields()) {
                            FieldMappingEntry fieldEntry = findClientField(classEntry, field.getSrcName());

                            if (fieldEntry == null) {
                                debug("Mappings found for [" + field.getSrcName() + " -> " + field.getDstName(0) + "] but no matching intermediary method was found");
                                continue;
                            }

                            fieldEntry.mcp.put(mappingSet, inputOrEmpty(field.getDstName(0)));
                        }

                        String destinationName = item.getDstName(0) != null ? item.getDstName(0) : item.getSrcName();
                        classEntry.mcp.put(mappingSet, ClassPath.fromName(destinationName));
                    }
                    case MCP_SERVER -> {
                        classEntry = findServerClass(item.getSrcName());

                        if (classEntry == null) {
                            debug("Mappings found for [" + item.getSrcName() + "." + item.getSrcName() + " -> " + item.getDstName(0) + "." + item.getDstName(0) + "] but no matching intermediary class was found");
                            continue;
                        }

                        if (classEntry.environment == Environment.CLIENT) {
                            System.out.println(classEntry);
                            System.out.println("[" + item.getSrcName() + " -> " + item.getDstName(0) + "]");
                            throw new IllegalStateException("Found a matching obfuscated name for server but the environment is client");
                        }

                        /// Methods
                        for (var method : item.getMethods()) {
                            MethodMappingEntry methodEntry = findServerMethod(classEntry, method.getSrcDesc(), method.getSrcName());

                            if (methodEntry == null) {
                                debug("Mappings found for [" + item.getSrcName() + "." + method.getSrcName() + " -> " + item.getDstName(0) + "." + method.getDstName(0) + "] but no matching intermediary field was found");
                                continue;
                            }

                            Method methodMapping = new Method();

                            // Name
                            methodMapping.name = inputOrEmpty(method.getDstName(0));

                            // Desc
                            methodMapping.desc = method.getDstDesc(0);

                            // Args
                            ArrayList<String> args = new ArrayList<>();
                            for (var argument : method.getArgs()) {
                                args.add(argument.getDstName(0));
                            }
                            methodMapping.args = args;

                            methodEntry.mcp.put(mappingSet, methodMapping);
                        }

                        /// Fields
                        for (var field : item.getFields()) {
                            FieldMappingEntry fieldEntry = findServerField(classEntry, field.getSrcName());

                            if (fieldEntry == null) {
                                debug("Mappings found for [" + field.getSrcName() + " -> " + field.getDstName(0) + "] but no matching intermediary method was found");
                                continue;
                            }

                            fieldEntry.mcp.put(mappingSet, inputOrEmpty(field.getDstName(0)));
                        }

                        String destinationName = item.getDstName(0) != null ? item.getDstName(0) : item.getSrcName();
                        classEntry.mcp.put(mappingSet, ClassPath.fromName(destinationName));
                    }
                }

            }

            info("MCP mappings " + mappingSet.name + " loaded");
        } catch (IOException e) {
            error("Failed to load mappings " + mappingSet);
        }
    }

    public ClassMappingEntry findClientClass(String obfClient) {
        for (var classEntry : classes) {
            if (classEntry.obfuscatedClient.equalsIgnoreCase(obfClient)) {
                return classEntry;
            }
        }
        return null;
    }

    public ClassMappingEntry findServerClass(String obfServer) {
        for (var classEntry : classes) {
            if (classEntry.obfuscatedServer.equalsIgnoreCase(obfServer)) {
                return classEntry;
            }
        }
        return null;
    }

    public MethodMappingEntry findClientMethod(ClassMappingEntry classEntry, String obfClientDesc, String obfClient) {
        for (var methodEntry : classEntry.methods) {
            if (methodEntry.obfuscatedClient.desc.equals(obfClientDesc) && methodEntry.obfuscatedClient.equals(obfClient)) {
                return methodEntry;
            }
        }
        return null;
    }

    public MethodMappingEntry findServerMethod(ClassMappingEntry classEntry, String obfServerDesc, String obfServer) {
        for (var methodEntry : classEntry.methods) {
            if (methodEntry.obfuscatedServer.desc.equals(obfServerDesc) && methodEntry.obfuscatedServer.equals(obfServer)) {
                return methodEntry;
            }
        }
        return null;
    }

    public FieldMappingEntry findClientField(ClassMappingEntry classEntry, String obfClient) {
        for (var fieldEntry : classEntry.fields) {
            if (fieldEntry.obfuscatedClient.equals(obfClient)) {
                return fieldEntry;
            }
        }
        return null;
    }

    public FieldMappingEntry findServerField(ClassMappingEntry classEntry, String obfServer) {
        for (var fieldEntry : classEntry.fields) {
            if (fieldEntry.obfuscatedClient.equals(obfServer)) {
                return fieldEntry;
            }
        }
        return null;
    }

    // Intermediary
    public void loadIntermediary(Mappings mappingSet) {
        info("Loading " + mappingSet.name + " Intermediary");
        VisitableMappingTree intermediary = new MemoryMappingTree();
        try {
            MappingReader.read(Util.getMappingPath(mappingSet.path), mappingSet.format, intermediary);

            for (var item : intermediary.getClasses()) {
                /// Class
                // Generate Class Path
                ClassPath classPath = ClassPath.fromName(item.getSrcName());

                // Ignore argo library
                if (classPath.pkg.contains("argo")) {
                    continue;
                }

                // Create the Entry
                ClassMappingEntry classEntry = new ClassMappingEntry();

                // If both of them are null (like in cursed-legacy) then the check would always assume its client
                if (item.getName("server") == null && item.getName("client") == null) {

                    // This is only here for unobfuscated classes
                    if (classPath.pkg.equals("net.minecraft.client") || classPath.pkg.equals("net.minecraft.isom")) {
                        item.setDstName(item.getSrcName(), intermediary.getNamespaceId("client"));
                        item.setDstName(item.getSrcName(), intermediary.getNamespaceId("server"));
                        classEntry.environment = Environment.CLIENT;
                        classEntry.obfuscatedClient = item.getSrcName();

                    } else if (classPath.pkg.equals("net.minecraft.server")) {
                        item.setDstName(item.getSrcName(), intermediary.getNamespaceId("server"));
                        item.setDstName(item.getSrcName(), intermediary.getNamespaceId("client"));
                        classEntry.environment = Environment.SERVER;
                        classEntry.obfuscatedServer = item.getSrcName();
                    }

                    // For anything else, proceed as usual
                } else {
                    // Determine Side
                    if (item.getSrcName().equals(item.getName("server")) && !classPath.pkg.contains("server")) {
                        classEntry.environment = Environment.CLIENT;
                    } else if (item.getSrcName().equals(item.getName("client")) && !(classPath.pkg.contains("client") || classPath.pkg.contains("isom"))) {
                        classEntry.environment = Environment.SERVER;
                    } else {
                        classEntry.environment = Environment.MERGED;
                    }

                    // Obfuscated
                    classEntry.obfuscatedClient = item.getName("client");
                    classEntry.obfuscatedServer = item.getName("server");

                    if (classEntry.obfuscatedClient == null) {
                        classEntry.obfuscatedClient = "";
                    }

                    if (classEntry.obfuscatedServer == null) {
                        classEntry.obfuscatedServer = "";
                    }
                }

                if (classEntry.environment == Environment.SERVER) {
                    classEntry.obfuscatedClient = "";
                }

                if (classEntry.environment == Environment.CLIENT) {
                    classEntry.obfuscatedServer = "";
                }

                // Attempt to find an existing entry
                ClassMappingEntry existingEntry = findIntermediaryClass(classEntry.obfuscatedClient, classEntry.obfuscatedServer);
                if (existingEntry != null) {
                    classEntry = existingEntry;
                }

                // Intermediary
                classEntry.intermediary.put(mappingSet, classPath);

                /// Methods
                for (MappingTree.MethodMapping method : item.getMethods()) {
                    // Create the entry
                    MethodMappingEntry methodEntry = new MethodMappingEntry();

                    // Set the default environment as merged
                    methodEntry.environment = Environment.MERGED;

                    // Client
                    // If the client and intermediary names are the same, it doesnt exist on client
                    if (method.getName("client") == null || method.getName("client").equals(method.getSrcName())) {
                        methodEntry.obfuscatedClient.name = "";
                        methodEntry.obfuscatedClient.desc = "";
                        methodEntry.environment = Environment.SERVER;

                        methodEntry.obfuscatedClient.args = new ArrayList<>();
                    } else {
                        methodEntry.obfuscatedClient.name = method.getName("client");
                        methodEntry.obfuscatedClient.desc = method.getDesc("client");

                        ArrayList<String> args = new ArrayList<>();
                        for (var argument : method.getArgs()) {
                            args.add(argument.getName("client"));
                        }
                        methodEntry.obfuscatedClient.args = args;
                    }

                    // Server
                    // If the server and intermnediary names are the same, it doesnt exist on the server
                    if (method.getName("server") == null || method.getName("server").equals(method.getSrcName())) {
                        methodEntry.obfuscatedServer.name = "";
                        methodEntry.obfuscatedServer.desc = "";
                        methodEntry.environment = Environment.CLIENT;

                        methodEntry.obfuscatedServer.args = new ArrayList<>();
                    } else {
                        methodEntry.obfuscatedServer.name = method.getName("server");
                        methodEntry.obfuscatedServer.desc = method.getDesc("server");

                        ArrayList<String> args = new ArrayList<>();
                        for (var argument : method.getArgs()) {
                            args.add(argument.getName("server"));
                        }
                        methodEntry.obfuscatedServer.args = args;
                    }

                    MethodMappingEntry existingMethodEntry = findIntermediaryMethod(classEntry, methodEntry.obfuscatedClient.name, methodEntry.obfuscatedServer.name, methodEntry.obfuscatedClient.desc, methodEntry.obfuscatedServer.desc);
                    if (existingMethodEntry != null) {
                        methodEntry = existingMethodEntry;
                    }

                    // Intermediary
                    Method methodMapping = new Method();
                    methodMapping.name = method.getSrcName();
                    methodMapping.desc = method.getSrcDesc();
                    methodEntry.intermediary.put(mappingSet, methodMapping);

                    if (existingMethodEntry == null) {
                        classEntry.methods.add(methodEntry);
                    }
                }

                /// Fields
                for (MappingTree.FieldMapping field : item.getFields()) {
                    var fieldEntry = new FieldMappingEntry();

                    if (field.getName("client") == null || field.getName("client").equals(field.getSrcName())) {
                        fieldEntry.obfuscatedClient = "";
                    } else {
                        fieldEntry.obfuscatedClient = field.getName("client");
                    }

                    if (field.getName("server") == null || field.getName("server").equals(field.getSrcName())) {
                        fieldEntry.obfuscatedServer = "";
                    } else {
                        fieldEntry.obfuscatedServer = field.getName("server");
                    }

                    FieldMappingEntry existingFieldEntry = findIntermediaryField(classEntry, fieldEntry.obfuscatedClient, fieldEntry.obfuscatedServer);
                    if (existingFieldEntry != null) {
                        fieldEntry = existingFieldEntry;
                    }

                    fieldEntry.intermediary.put(mappingSet, field.getSrcName());

                    if (existingFieldEntry == null) {
                        classEntry.fields.add(fieldEntry);
                    }
                }

                // Put the class in classes map
                if (existingEntry == null) {
                    classes.add(classEntry);
                }
            }

        } catch (IOException e) {
            error("Failed to load intermediary, file not found");
            e.printStackTrace();
        }

        info("Intermediary " + mappingSet.id + " loaded");
    }

    public ClassMappingEntry findIntermediaryClass(String obfClient, String obfServer) {
        for (var classEntry : classes) {
            try {
                if (classEntry.obfuscatedClient.equalsIgnoreCase(obfClient) && classEntry.obfuscatedServer.equalsIgnoreCase(obfServer)) {
                    return classEntry;
                }
            } catch (NullPointerException e) {
                error("NPE!");
                e.printStackTrace();
                error(classEntry.toString());
            }
        }
        return null;
    }

    public MethodMappingEntry findIntermediaryMethod(ClassMappingEntry classEntry, String obfClient, String obfServer, String obfClientDesc, String obfServerDesc) {
        for (var methodEntry : classEntry.methods) {
            if (methodEntry.obfuscatedClient.desc.equals(obfClientDesc) && methodEntry.obfuscatedServer.desc.equals(obfServerDesc) && methodEntry.obfuscatedClient.name.equals(obfClient) && methodEntry.obfuscatedServer.name.equals(obfServer)) {
                return methodEntry;
            }
        }
        return null;
    }

    public FieldMappingEntry findIntermediaryField(ClassMappingEntry classEntry, String obfClient, String obfServer) {
        for (var fieldEntry : classEntry.fields) {
            if (fieldEntry.obfuscatedClient.equals(obfClient) && fieldEntry.obfuscatedServer.equals(obfServer)) {
                return fieldEntry;
            }
        }
        return null;
    }
}
