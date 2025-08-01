package net.danygames2014.nyaview.mapping;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.Util;
import net.danygames2014.nyaview.mapping.entry.*;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.fabricmc.mappingio.tree.VisitableMappingTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static net.danygames2014.nyaview.NyaView.LOGGER;
import static net.danygames2014.nyaview.Util.inputOrEmpty;

public class MappingLoader {
    // ID -> Mappings
    public final HashMap<String, Mappings> mappings;
    public final HashMap<String, Intermediary> intermediaries;
    public final ArrayList<ClassMappingEntry> classes;

    public MappingLoader() {
        mappings = new HashMap<>();
        intermediaries = new HashMap<>();
        classes = new ArrayList<>();
    }

    public void load(Intermediary intermediary) {
        LOGGER.debug("Loading Intermediary " + intermediary.name);
        if (!intermediaries.containsValue(intermediary)) {
            loadIntermediary(intermediary);
            intermediaries.put(intermediary.id, intermediary);
        } else {
            LOGGER.warn("Tried to load intermediary that is already loaded: " + intermediary.name);
        }
    }

    public void load(Mappings mappingSet) {
        LOGGER.debug("Loading mappings " + mappingSet.name);
        if (!mappings.containsValue(mappingSet)) {
            switch (mappingSet.type) {
                case MCP -> {
                    loadMcp(mappingSet);
                }
                case BABRIC -> {
                    loadBabric(mappingSet);
                }
            }

            mappings.put(mappingSet.id, mappingSet);
        } else {
            LOGGER.warn("Tried to load mappings that are already loaded: " + mappingSet.name);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private void loadIntermediary(Intermediary intermediarySet) {
        LOGGER.info("Loading Intermediary " + intermediarySet.name);
        VisitableMappingTree intermediary = new MemoryMappingTree();

        try {
            MappingReader.read(Util.getMappingPath(intermediarySet.path), intermediarySet.format, intermediary);

            for (var item : intermediary.getClasses()) {
                // Class
                // Generate Class Path
                String intermediaryName = item.getName(intermediarySet.intermediaryName);
                if (intermediaryName == null) {
                    LOGGER.warn("Class " + item + " has no intermediary name");
                    continue;
                }

                ClassPath classPath = ClassPath.fromName(intermediaryName);
                LOGGER.trace("Loading Class " + classPath.getFullPath());

                // Check if package is ignored
                boolean ignored = false;
                for (String ignoredPackage : NyaView.profileManager.activeProfile.getIgnoredPackages()) {
                    if (classPath.pkg.contains(ignoredPackage)) {
                        LOGGER.warn("Ignored class " + classPath.getFullPath() + " due to being in package " + ignoredPackage);
                        ignored = true;
                        break;
                    }
                }

                if (ignored) {
                    continue;
                }

                // Create Class Entry
                ClassMappingEntry classEntry = new ClassMappingEntry();

                // If the intermediary is merged, we have to determine the side for each class
                switch (intermediarySet.environment) {
                    case CLIENT -> {
                        // Othwerwise, if the intermediary is only for one side, we don't need to determine the side
                        classEntry.environment = Environment.CLIENT;
                        classEntry.obfuscatedClient = item.getName(intermediarySet.clientName);
                    }

                    case SERVER -> {
                        // Othwerwise, if the intermediary is only for one side, we don't need to determine the side
                        classEntry.environment = Environment.SERVER;
                        classEntry.obfuscatedServer = item.getName(intermediarySet.serverName);

                    }

                    case MERGED -> {
                        // If both of them are null (like in cursed-legacy) then the check would always assume its client
                        if (item.getName(intermediarySet.serverName) == null && item.getName(intermediarySet.clientName) == null) {
                            // This is only here for unobfuscated classes
                            if (classPath.pkg.equals("net.minecraft.client") || classPath.pkg.equals("net.minecraft.isom")) {
                                item.setDstName(item.getName(intermediarySet.intermediaryName), intermediary.getNamespaceId(intermediarySet.clientName));
                                item.setDstName(item.getName(intermediarySet.intermediaryName), intermediary.getNamespaceId(intermediarySet.serverName));
                                classEntry.environment = Environment.CLIENT;
                                classEntry.obfuscatedClient = item.getName(intermediarySet.intermediaryName);
                            } else if (classPath.pkg.equals("net.minecraft.server")) {
                                item.setDstName(item.getName(intermediarySet.intermediaryName), intermediary.getNamespaceId(intermediarySet.serverName));
                                item.setDstName(item.getName(intermediarySet.intermediaryName), intermediary.getNamespaceId(intermediarySet.clientName));
                                classEntry.environment = Environment.SERVER;
                                classEntry.obfuscatedServer = item.getName(intermediarySet.intermediaryName);
                            }

                            // For anything else, proceed as usual
                        } else {
                            // Determine Side
                            if (item.getName(intermediarySet.intermediaryName).equals(item.getName(intermediarySet.serverName)) && !classPath.pkg.contains("server")) {
                                classEntry.environment = Environment.CLIENT;
                            } else if (item.getName(intermediarySet.intermediaryName).equals(item.getName(intermediarySet.clientName)) && !(classPath.pkg.contains("client") || classPath.pkg.contains("isom"))) {
                                classEntry.environment = Environment.SERVER;
                            } else {
                                classEntry.environment = Environment.MERGED;
                            }

                            // Obfuscated
                            classEntry.obfuscatedClient = item.getName(intermediarySet.clientName);
                            classEntry.obfuscatedServer = item.getName(intermediarySet.serverName);
                        }

                        // Make sure the obfuscated fields arent null, but empty
                        if (classEntry.obfuscatedClient == null) {
                            classEntry.obfuscatedClient = "";
                        }

                        if (classEntry.obfuscatedServer == null) {
                            classEntry.obfuscatedServer = "";
                        }
                    }
                }

                // If the environment is server, make sure the client obfuscated field is empty
                if (classEntry.environment == Environment.SERVER) {
                    classEntry.obfuscatedClient = "";
                }

                // If the environment is client, make sure the server obfuscated field is empty
                if (classEntry.environment == Environment.CLIENT) {
                    classEntry.obfuscatedServer = "";
                }

                // Attempt to find an existing entry
                ClassMappingEntry existingEntry = findClass(classEntry.obfuscatedClient, classEntry.obfuscatedServer, intermediarySet.environment);
                if (existingEntry != null) {
                    classEntry = existingEntry;
                }

                // Intermediary Class Name
                classEntry.intermediary.put(intermediarySet, classPath);

                /* Methods */
                for (MappingTree.MethodMapping method : item.getMethods()) {
                    // Create the entry
                    MethodMappingEntry methodEntry = new MethodMappingEntry(classEntry);

                    // Set the default environment as merged
                    methodEntry.environment = Environment.MERGED;

                    // Client
                    // If the client and intermediary names are the same, it doesnt exist on client
                    if (intermediarySet.environment == Environment.SERVER || method.getName(intermediarySet.clientName) == null || method.getName(intermediarySet.clientName).equals(method.getName(intermediarySet.intermediaryName))) {
                        methodEntry.obfuscatedClient.name = "";
                        methodEntry.obfuscatedClient.desc = "";
                        methodEntry.environment = Environment.SERVER;

                        methodEntry.obfuscatedClient.args = new ArrayList<>();
                    } else {
                        methodEntry.obfuscatedClient.name = method.getName(intermediarySet.clientName);
                        methodEntry.obfuscatedClient.desc = method.getDesc(intermediarySet.clientName);

                        ArrayList<String> args = new ArrayList<>();
                        for (var argument : method.getArgs()) {
                            args.add(argument.getName(intermediarySet.clientName));
                        }
                        methodEntry.obfuscatedClient.args = args;
                    }

                    // Server
                    // If the server and intermnediary names are the same, it doesnt exist on the server
                    if (intermediarySet.environment == Environment.CLIENT || method.getName(intermediarySet.serverName) == null || method.getName(intermediarySet.serverName).equals(method.getName(intermediarySet.intermediaryName))) {
                        methodEntry.obfuscatedServer.name = "";
                        methodEntry.obfuscatedServer.desc = "";
                        methodEntry.environment = Environment.CLIENT;

                        methodEntry.obfuscatedServer.args = new ArrayList<>();
                    } else {
                        methodEntry.obfuscatedServer.name = method.getName(intermediarySet.serverName);
                        methodEntry.obfuscatedServer.desc = method.getDesc(intermediarySet.serverName);

                        ArrayList<String> args = new ArrayList<>();
                        for (var argument : method.getArgs()) {
                            args.add(argument.getName(intermediarySet.serverName));
                        }
                        methodEntry.obfuscatedServer.args = args;
                    }

                    MethodMappingEntry existingMethodEntry = findMethod(classEntry, methodEntry.obfuscatedClient.name, methodEntry.obfuscatedServer.name, methodEntry.obfuscatedClient.desc, methodEntry.obfuscatedServer.desc, methodEntry.environment);
                    if (existingMethodEntry != null) {
                        methodEntry = existingMethodEntry;
                    }

                    // Intermediary
                    Method methodMapping = new Method();
                    methodMapping.name = method.getName(intermediarySet.intermediaryName);
                    methodMapping.desc = method.getDesc(intermediarySet.intermediaryName);
                    methodEntry.intermediary.put(intermediarySet, methodMapping);

                    if (existingMethodEntry == null) {
                        classEntry.methods.add(methodEntry);
                    }
                }

                /* Fields */
                for (MappingTree.FieldMapping field : item.getFields()) {
                    var fieldEntry = new FieldMappingEntry(classEntry);

                    if (intermediarySet.environment == Environment.SERVER || field.getName(intermediarySet.clientName) == null || field.getName(intermediarySet.clientName).equals(field.getName(intermediarySet.intermediaryName))) {
                        fieldEntry.obfuscatedClient = "";
                    } else {
                        fieldEntry.obfuscatedClient = field.getName(intermediarySet.clientName);
                    }

                    if (intermediarySet.environment == Environment.CLIENT || field.getName(intermediarySet.serverName) == null || field.getName(intermediarySet.serverName).equals(field.getName(intermediarySet.intermediaryName))) {
                        fieldEntry.obfuscatedServer = "";
                    } else {
                        fieldEntry.obfuscatedServer = field.getName(intermediarySet.serverName);
                    }
                    
                    FieldMappingEntry existingFieldEntry = findField(classEntry, fieldEntry.obfuscatedClient, fieldEntry.obfuscatedServer, intermediarySet.environment);
                    if (existingFieldEntry != null) {
                        fieldEntry = existingFieldEntry;
                    }

                    fieldEntry.intermediary.put(intermediarySet, field.getName(intermediarySet.intermediaryName));

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
            LOGGER.error("Failed to load intermediary " + intermediarySet.name, e);
            return;
        }

        LOGGER.info("Intermediary " + intermediarySet.name + " loaded");
    }

    private void loadMcp(Mappings mappingSet) {
        LOGGER.info("Loading " + mappingSet.name + " MCP Mappings");
        VisitableMappingTree mcp = new MemoryMappingTree();

        try {
            MappingReader.read(Util.getMappingPath(mappingSet.path), mappingSet.format, mcp);
            Environment env = mappingSet.environment;

            for (var item : mcp.getClasses()) {
                /* Class */
                // Generate Class Path
                ClassPath classPath = ClassPath.fromName(item.getSrcName());

                // Create a Class Entry
                ClassMappingEntry classEntry = null;

                // Find the class to add a mapping to
                switch (env) {
                    case CLIENT -> {
                        classEntry = findClass(item.getSrcName(), null, Environment.CLIENT);

                        if (classEntry != null && classEntry.environment == Environment.SERVER) {
                            System.out.println(classEntry);
                            System.out.println("[" + item.getSrcName() + " -> " + item.getDstName(0) + "]");
                            throw new IllegalStateException("Found a matching obfuscated name for client but the environment is server");
                        }
                    }

                    case SERVER -> {
                        classEntry = findClass(null, item.getSrcName(), Environment.SERVER);
                    }
                }

                if (classEntry == null) {
                    LOGGER.warn("Mappings found for [" + item.getSrcName() + " -> " + item.getDstName(0) + "] but no matching class was found");
                    continue;
                }

                /* Methods */
                for (var method : item.getMethods()) {
                    MethodMappingEntry methodEntry = findMethod(classEntry, method.getSrcName(), method.getSrcName(), method.getSrcDesc(), method.getSrcDesc(), env);

                    if (methodEntry == null) {
                        LOGGER.debug("Mappings found for [" + item.getSrcName() + "." + method.getSrcName() + " -> " + item.getDstName(0) + "." + method.getDstName(0) + "] but no matching method was found");
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

                /* Fields */
                for (var field : item.getFields()) {
                    FieldMappingEntry fieldEntry = findField(classEntry, field.getSrcName(), field.getSrcName(), env);

                    if (fieldEntry == null) {
                        LOGGER.debug("Mappings found for [" + field.getSrcName() + " -> " + field.getDstName(0) + "] but no matching field was found");
                        continue;
                    }

                    fieldEntry.mcp.put(mappingSet, inputOrEmpty(field.getDstName(0)));
                }

                String destinationName = item.getDstName(0) != null ? item.getDstName(0) : item.getSrcName();
                classEntry.mcp.put(mappingSet, ClassPath.fromName(destinationName));
            }

        } catch (IOException e) {
            LOGGER.error("Failed to load " + mappingSet.name + " MCP mappings ", e);
            return;
        }

        LOGGER.info("MCP Mappings " + mappingSet.name + " loaded");
    }

    private ClassMappingEntry findClass(String obfClient, String obfServer, Environment environment) {
        for (ClassMappingEntry classEntry : classes) {
            try {
                switch (environment) {
                    case CLIENT -> {
                        if (classEntry.obfuscatedClient.equalsIgnoreCase(obfClient)) {
                            return classEntry;
                        }
                    }

                    case SERVER -> {
                        if (classEntry.obfuscatedServer.equalsIgnoreCase(obfServer)) {
                            return classEntry;
                        }
                    }

                    case MERGED -> {
                        if (classEntry.obfuscatedClient.equalsIgnoreCase(obfClient) && classEntry.obfuscatedServer.equalsIgnoreCase(obfServer)) {
                            return classEntry;
                        }
                    }
                }
            } catch (NullPointerException e) {
                LOGGER.error("Failed to find class. obfClient:" + obfClient + " obfServer:" + obfServer, e);
                LOGGER.error("Currenty Class Entry: \n" + classEntry.toString());
            }
        }
        return null;
    }

    private MethodMappingEntry findMethod(ClassMappingEntry classEntry, String obfClient, String obfServer, String obfClientDesc, String obfServerDesc, Environment environment) {
        for (var methodEntry : classEntry.methods) {
            switch (environment) {
                case CLIENT -> {
                    if (methodEntry.obfuscatedClient.desc.equals(obfClientDesc) && methodEntry.obfuscatedClient.name.equals(obfClient)) {
                        return methodEntry;
                    }
                }
                case SERVER -> {
                    if (methodEntry.obfuscatedServer.desc.equals(obfServerDesc) && methodEntry.obfuscatedServer.name.equals(obfServer)) {
                        return methodEntry;
                    }
                }
                case MERGED -> {
                    if (methodEntry.obfuscatedClient.desc.equals(obfClientDesc) && methodEntry.obfuscatedServer.desc.equals(obfServerDesc) && methodEntry.obfuscatedClient.name.equals(obfClient) && methodEntry.obfuscatedServer.name.equals(obfServer)) {
                        return methodEntry;
                    }
                }
            }
        }
        return null;
    }

    private FieldMappingEntry findField(ClassMappingEntry classEntry, String obfClient, String obfServer, Environment environment) {
        for (var fieldEntry : classEntry.fields) {
            switch (environment) {
                case CLIENT -> {
                    if (fieldEntry.obfuscatedClient.equals(obfClient)) {
                        return fieldEntry;
                    }
                }
                case SERVER -> {
                    if (fieldEntry.obfuscatedServer.equals(obfServer)) {
                        return fieldEntry;
                    }
                }
                case MERGED -> {
                    if (fieldEntry.obfuscatedClient.equals(obfClient) && fieldEntry.obfuscatedServer.equals(obfServer)) {
                        return fieldEntry;
                    }
                }
            }
        }
        return null;
    }

    private void loadBabric(Mappings mappingSet) {
        LOGGER.info("Loading " + mappingSet.name + " Babric Mappings");
        VisitableMappingTree babric = new MemoryMappingTree();

        try {
            MappingReader.read(Util.getMappingPath(mappingSet.path), mappingSet.format, babric);

            Intermediary intermediary = NyaView.profileManager.activeProfile.getIntermediary(mappingSet.intermediaryId);
            if (intermediary != null) {
                LOGGER.info("Intermediary " + intermediary.name + " discovered for " + mappingSet.name);
            } else {
                LOGGER.error("Intermediary " + mappingSet.name + " not found for " + mappingSet.name);
                return;
            }

            for (var item : babric.getClasses()) {
                /* Class */
                // Generate Class Path
                ClassPath classPath = ClassPath.fromName(item.getSrcName());

                // Find the class
                ClassMappingEntry classEntry = findIntermediaryClass(classPath, intermediary);

                // If no class was found, log and ignore the mapping
                if (classEntry == null) {
                    LOGGER.debug("Mappings found for [" + item.getSrcName() + " -> " + item.getDstName(0) + "] but no matching intermediary class was found");
                    continue;
                }

                /* Methods */
                for (var method : item.getMethods()) {
                    MethodMappingEntry methodEntry = findIntermediaryMethod(classEntry, intermediary, method.getSrcName(), method.getSrcDesc());

                    if (methodEntry == null) {
                        LOGGER.debug("Mappings found for [" + item.getSrcName() + "." + method.getSrcName() + " -> " + item.getDstName(0) + "." + method.getDstName(0) + "] but no matching intermediary method was found");
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

                /* Fields */
                for (var field : item.getFields()) {
                    FieldMappingEntry fieldEntry = findIntermediaryField(classEntry, intermediary, field.getSrcName());

                    if (fieldEntry == null) {
                        LOGGER.debug("Mappings found for [" + item.getSrcName() + "." + field.getSrcName() + " -> " + item.getDstName(0) + "." + field.getDstName(0) + "] but no matching intermediary method was found");
                        continue;
                    }

                    fieldEntry.babric.put(mappingSet, inputOrEmpty(field.getDstName(0)));
                }

                String destinationName = item.getDstName(0) != null ? item.getDstName(0) : item.getSrcName();
                classEntry.babric.put(mappingSet, ClassPath.fromName(destinationName));
            }

        } catch (IOException e) {
            LOGGER.error("Failed to load " + mappingSet.name + " Babric mappings ", e);
            return;
        }

        LOGGER.info("Babric Mappings " + mappingSet.name + " loaded");
    }

    public ClassMappingEntry findIntermediaryClass(ClassPath intermediaryPath, Intermediary intermediary) {
        for (var classEntry : classes) {
            if (classEntry.intermediary.containsKey(intermediary) && classEntry.intermediary.get(intermediary).equals(intermediaryPath)) {
                return classEntry;
            }
        }
        return null;
    }

    public MethodMappingEntry findIntermediaryMethod(ClassMappingEntry classEntry, Intermediary intermediary, String intermediaryName, String intermediaryDesc) {
        for (var method : classEntry.methods) {
            if (method.getIntermediaryName(intermediary).equals(intermediaryName) && method.getIntermediaryDesc(intermediary).equals(intermediaryDesc)) {
                return method;
            }
        }
        return null;
    }

    public FieldMappingEntry findIntermediaryField(ClassMappingEntry classEntry, Intermediary intermediary, String intermediaryName) {
        for (var field : classEntry.fields) {
            if (field.getIntermediaryName(intermediary).equals(intermediaryName)) {
                return field;
            }
        }
        return null;
    }
}
