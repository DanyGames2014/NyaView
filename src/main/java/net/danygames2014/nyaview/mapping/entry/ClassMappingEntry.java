package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;
import net.danygames2014.nyaview.util.Search;
import net.danygames2014.nyaview.util.Searchable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.util.Search.SearchMappings.*;
import static net.danygames2014.nyaview.util.Util.filter;

public class ClassMappingEntry implements Searchable {
    // Environment
    public Environment environment;

    // Obfuscated
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP Names (Obfuscated -> Named)
    public HashMap<Mappings, ClassPath> mcp;

    // Intermediaries (Obfuscated -> Intermediary)
    public HashMap<Mappings, ClassPath> intermediary;

    // Fabric (Intermediary -> Named)
    public HashMap<Mappings, ClassPath> babric;

    // Methods
    public ArrayList<MethodMappingEntry> methods;

    // Fields
    public ArrayList<FieldMappingEntry> fields;


    public ClassMappingEntry() {
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
        babric = new HashMap<>();
        methods = new ArrayList<>();
        fields = new ArrayList<>();
    }

    // MCP
    public ClassPath getMcpName(Mappings mappings) {
        return mcp.get(mappings);
    }

    public ClassPath getMcpName(String id) {
        return mcp.get(NyaView.config.getMappings(id));
    }

    // Intermediary
    public ClassPath getIntermediaryName(Mappings mappings) {
        return intermediary.get(mappings);
    }

    public ClassPath getIntermediaryName(String id) {
        return intermediary.get(NyaView.config.getMappings(id));
    }

    // Fabric
    public ClassPath getBabricName(Mappings mappings) {
        return babric.get(mappings);
    }

    public ClassPath getBabricName(String id) {
        return babric.get(NyaView.config.getMappings(id));
    }

    public boolean searchMatch(String query) {
        if ((obfuscatedClient).toLowerCase().strip().contains(query)) {
            return true;
        }

        if ((obfuscatedServer).toLowerCase().strip().contains(query)) {
            return true;
        }

        for (ClassPath item : babric.values()) {
            if (item.name.toLowerCase().strip().contains(query)) {
                return true;
            }
        }

        for (ClassPath item : intermediary.values()) {
            if (item.name.toLowerCase().strip().equals(query)) {
                return true;
            }
        }

        for (ClassPath item : mcp.values()) {
            if (item.name.toLowerCase().strip().contains(query)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean match(Search.SearchParameters parameters) {
        if (parameters.searchType == Search.SearchType.CLASS || parameters.searchType == Search.SearchType.CARPET_BOMB) {
            if (parameters.mappings == INTERMEDIARY || parameters.mappings == ALL) {
                for (ClassPath item : intermediary.values()) {
                    if (filter(item.name, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_CLIENT || parameters.mappings == ALL) {
                if (filter(obfuscatedClient, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_SERVER || parameters.mappings == ALL) {
                if (filter(obfuscatedServer, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == BABRIC || parameters.mappings == ALL) {
                for (ClassPath item : babric.values()) {
                    if (filter(item.name, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_CLIENT || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, ClassPath> item : mcp.entrySet()) {
                    if (item.getKey().type == MappingType.MCP_CLIENT) {
                        if (filter(item.getValue().name, parameters)) {
                            return true;
                        }
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_SERVER || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, ClassPath> item : mcp.entrySet()) {
                    if (item.getKey().type == MappingType.MCP_SERVER) {
                        if (filter(item.getValue().name, parameters)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return
                "\nCLASS" +
                        "\n   environment = " + environment +
                        "\n   obfuscatedClient = " + obfuscatedClient +
                        "\n   obfuscatedServer = " + obfuscatedServer +
                        "\n   mcp = " + mcp +
                        "\n   intermediary = " + intermediary +
                        "\n   babric = " + babric +
                        "\n   methods = " + methods +
                        "\n   fields = " + fields;
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String niceString(boolean superstrict) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("\nCLASS");

        // Environment
        sb.append("\n   Environment : " + environment);

        // Obfuscated
        if (environment == Environment.CLIENT || environment == Environment.MERGED) {
            sb.append("\n   Obfuscated Client : ").append(obfuscatedClient);
        }

        if (environment == Environment.SERVER || environment == Environment.MERGED) {
            sb.append("\n   Obfuscated Server : " + obfuscatedServer);
        }

        // Intermediary
        sb.append("\n   Intermediary : ");
        for (var mapping : intermediary.entrySet()) {
            sb.append(mapping.getKey().name + "=" + mapping.getValue().name + "  ");
        }

        // Named
        sb.append("\n   Mappings : ");
//        sb.append("\n    ");
        for (var mapping : mcp.entrySet()) {
            sb.append("\n     ");
            sb.append(mapping.getKey().name + "=" + mapping.getValue().name + "  ");
        }

//        sb.append("\n    ");
        for (var mapping : babric.entrySet()) {
            sb.append("\n     ");
            sb.append(mapping.getKey().name + "=" + mapping.getValue().name + "  ");
        }

        if(!superstrict){
            sb.append("\n   Methods : ");
            for (var method : methods){
                sb.append(method.niceString(false));
            }
            sb.append("\n   Fields : ");
            for (var fields : fields){
                sb.append(fields.niceString(false));
            }
        }

        return sb.toString();
    }
}
