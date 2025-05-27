package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.Searchable;
import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.search.DisplayParameters;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchParameters.SearchMappings;
import net.danygames2014.nyaview.search.SearchParameters.SearchType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.Util.filter;

public class ClassMappingEntry implements Searchable {
    // Environment of the class, whether it exists on server, client or both
    public Environment environment;

    // Obfuscated names
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP Names (Obfuscated -> Named)
    public final HashMap<Mappings, ClassPath> mcp;

    // Intermediaries (Obfuscated -> Intermediary)
    public final HashMap<Intermediary, ClassPath> intermediary;

    // Fabric (Intermediary -> Named)
    public final HashMap<Mappings, ClassPath> babric;

    // Methods
    public final ArrayList<MethodMappingEntry> methods;

    // Fields
    public final ArrayList<FieldMappingEntry> fields;

    public ClassMappingEntry() {
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
        babric = new HashMap<>();
        methods = new ArrayList<>();
        fields = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ClassMappingEntry{" +
                "environment=" + environment +
                ", obfuscatedClient='" + obfuscatedClient + '\'' +
                ", obfuscatedServer='" + obfuscatedServer + '\'' +
                ", mcp=" + mcp +
                ", intermediary=" + intermediary +
                ", babric=" + babric +
                ", methods=" + methods +
                ", fields=" + fields +
                '}';
    }

    @Override
    public boolean match(SearchParameters parameters) {
        // If we are searching a field or a method, dont bother
        if (parameters.type == SearchType.METHOD || parameters.type == SearchType.FIELD) {
            return false;
        }
        
        // Intermediary
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.INTERMEDIARY) {
            for (ClassPath item : intermediary.values()) {
                if (filter(item.name, parameters)) {
                    return true;
                }
            }
        }

        // Obfuscated Client
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.OBFUSCATED || parameters.mappings == SearchMappings.OBFUSCATED_CLIENT) {
            if (filter(obfuscatedClient, parameters)) {
                return true;
            }
        }

        // Obfuscated Server
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.OBFUSCATED || parameters.mappings == SearchMappings.OBFUSCATED_SERVER) {
            if (filter(obfuscatedServer, parameters)) {
                return true;
            }
        }

        // Fabric
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.FABRIC) {
            for (ClassPath item : babric.values()) {
                if (filter(item.name, parameters)) {
                    return true;
                }
            }
        }

        // MCP Client
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_CLIENT) {
            for (Map.Entry<Mappings, ClassPath> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.CLIENT) {
                    if (filter(item.getValue().name, parameters)) {
                        return true;
                    }
                }
            }
        }

        // MCP Server
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_SERVER) {
            for (Map.Entry<Mappings, ClassPath> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.SERVER) {
                    if (filter(item.getValue().name, parameters)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String searchString(DisplayParameters parameters) {
        return "";
    }

    public String niceString(boolean noChildren) {
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

        if (!noChildren) {
            if (!methods.isEmpty()) {
                sb.append("\n   Methods : ");
                for (var method : methods) {
                    sb.append(method.niceString(false));
                }
            }

            if (!fields.isEmpty()) {
                sb.append("\n   Fields : ");
                for (var fields : fields) {
                    sb.append(fields.niceString(false));
                }
            }
        }

        return sb.toString();
    }
}
