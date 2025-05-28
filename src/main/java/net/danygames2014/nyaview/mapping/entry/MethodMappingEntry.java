package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.search.Searchable;
import net.danygames2014.nyaview.descriptor.Descriptor;
import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchParameters.SearchMappings;
import net.danygames2014.nyaview.search.SearchParameters.SearchType;

import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.Util.filter;
import static net.danygames2014.nyaview.search.DisplayParameters.*;

public class MethodMappingEntry implements Searchable {
    // Parent Class
    public ClassMappingEntry classEntry;
    
    // Environment
    public Environment environment;

    // Obfuscated
    public Method obfuscatedClient;
    public Method obfuscatedServer;

    // Obfuscated Descriptor
    public String obfuscatedClientDesc;
    public String obfuscatedServerDesc;

    // MCP
    public final HashMap<Mappings, Method> mcp;

    // Intermediary
    public final HashMap<Intermediary, Method> intermediary;
    public final HashMap<Intermediary, String> intermediaryDesc;

    // Babric
    public final HashMap<Mappings, Method> babric;

    public MethodMappingEntry(ClassMappingEntry classEntry) {
        this.classEntry = classEntry;
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
        intermediaryDesc = new HashMap<>();
        babric = new HashMap<>();
        obfuscatedClient = new Method();
        obfuscatedServer = new Method();
    }

    // Intermediary
    public String getIntermediaryName(Intermediary intermediary) {
        if (!this.intermediary.containsKey(intermediary)) {
            return "";
        }

        return this.intermediary.get(intermediary).name;
    }

    // Intermediary Descriptor
    public String getIntermediaryDesc(Intermediary intermediary) {
        if (!this.intermediary.containsKey(intermediary)) {
            return "";
        }

        return this.intermediary.get(intermediary).desc;
    }

    @Override
    public String toString() {
        return "MethodMappingEntry{" +
                "environment=" + environment +
                ", obfuscatedClient=" + obfuscatedClient +
                ", obfuscatedServer=" + obfuscatedServer +
                ", obfuscatedClientDesc='" + obfuscatedClientDesc + '\'' +
                ", obfuscatedServerDesc='" + obfuscatedServerDesc + '\'' +
                ", mcp=" + mcp +
                ", intermediary=" + intermediary +
                ", intermediaryDesc=" + intermediaryDesc +
                ", babric=" + babric +
                '}';
    }

    @Override
    public boolean match(SearchParameters parameters) {
        // If we are searching for class or field, dont bother
        if (parameters.type == SearchType.CLASS || parameters.type == SearchType.FIELD) {
            return false;
        }

        // Intermediary
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.INTERMEDIARY) {
            for (Method item : intermediary.values()) {
                if (filter(item.name, parameters)) {
                    return true;
                }
            }
        }

        // Obfuscated Client
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.OBFUSCATED || parameters.mappings == SearchMappings.OBFUSCATED_CLIENT) {
            if (filter(obfuscatedClient.name, parameters)) {
                return true;
            }
        }

        // Obfuscated Server
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.OBFUSCATED || parameters.mappings == SearchMappings.OBFUSCATED_SERVER) {
            if (filter(obfuscatedServer.name, parameters)) {
                return true;
            }
        }

        // Fabric
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.FABRIC) {
            for (Method item : babric.values()) {
                if (filter(item.name, parameters)) {
                    return true;
                }
            }
        }

        // MCP Client
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_CLIENT) {
            for (Map.Entry<Mappings, Method> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.CLIENT) {
                    if (filter(item.getValue().name, parameters)) {
                        return true;
                    }
                }
            }
        }

        // MCP Server
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_SERVER) {
            for (Map.Entry<Mappings, Method> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.SERVER) {
                    if (filter(item.getValue().name, parameters)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public String searchString(Verbosity verbosity, boolean indent) {
        String offset = indent ? "      " : "";

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("\n" + offset + "METHOD");

        // Environment
        sb.append("\n" + offset + "Environment : " + environment);

        // Obfuscated
        if (environment == Environment.CLIENT || environment == Environment.MERGED) {
            sb.append("\n" + offset + "Obfuscated Client : " + Descriptor.niceString(obfuscatedClient));
        }

        if (environment == Environment.SERVER || environment == Environment.MERGED) {
            sb.append("\n" + offset + "Obfuscated Server : " + Descriptor.niceString(obfuscatedServer));
        }

        // Intermediary
        sb.append("\n" + offset + "Intermediary : ");
        for (var mapping : intermediary.entrySet()) {
            sb.append(mapping.getKey().name + "=" + mapping.getValue().name + "  ");
        }

        // Named
        sb.append("\n" + offset + "Mappings : ");
        for (var mapping : mcp.entrySet()) {
            sb.append("\n  " + offset);
            sb.append(mapping.getKey().name + ": " + Descriptor.niceString(mapping.getValue()) + "  ");
        }

        for (var mapping : babric.entrySet()) {
            sb.append("\n  " + offset);
            sb.append(mapping.getKey().name + ": " + Descriptor.niceString(mapping.getValue()) + "  ");
        }

        sb.append("\n");

        return sb.toString();
    }
}
