package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.search.OldSearch;
import net.danygames2014.nyaview.Searchable;
import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchParameters.SearchMappings;

import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.Util.filter;
import static net.danygames2014.nyaview.search.OldSearch.SearchMappings.*;
import static net.danygames2014.nyaview.Util.oldFilter;

public class FieldMappingEntry implements Searchable {
    // Parent Class
    public ClassMappingEntry classEntry;
    
    // Obfuscated
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP
    public final HashMap<Mappings, String> mcp;

    // Intermediary
    public final HashMap<Intermediary, String> intermediary;

    // Babric
    public final HashMap<Mappings, String> babric;

    public FieldMappingEntry(ClassMappingEntry classEntry) {
        this.classEntry = classEntry;
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
        babric = new HashMap<>();
    }

    // Intermediary
    public String getIntermediaryName(Intermediary mappings) {
        return intermediary.getOrDefault(mappings, "");
    }

    @Override
    public String toString() {
        return "FieldMappingEntry{" +
                "obfuscatedClient='" + obfuscatedClient + '\'' +
                ", obfuscatedServer='" + obfuscatedServer + '\'' +
                ", mcp=" + mcp +
                ", intermediary=" + intermediary +
                ", babric=" + babric +
                '}';
    }

    @Override
    public boolean match(SearchParameters parameters) {
        // If we are searching for class or method, dont bother
        if (parameters.type == SearchParameters.SearchType.CLASS || parameters.type == SearchParameters.SearchType.METHOD) {
            return false;
        }

        // Intermediary
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.INTERMEDIARY) {
            for (String item : intermediary.values()) {
                if (filter(item, parameters)) {
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
            for (String item : babric.values()) {
                if (filter(item, parameters)) {
                    return true;
                }
            }
        }
        
        // MCP Client
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_CLIENT) {
            for (Map.Entry<Mappings, String> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.CLIENT) {
                    if (filter(item.getValue(), parameters)) {
                        return true;
                    }
                }
            }
        }
        
        // MCP Server
        if (parameters.mappings == SearchMappings.ALL || parameters.mappings == SearchMappings.MCP || parameters.mappings == SearchMappings.MCP_SERVER) {
            for (Map.Entry<Mappings, String> item : mcp.entrySet()) {
                if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.SERVER) {
                    if (filter(item.getValue(), parameters)) {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    @Override
    public boolean oldMatch(OldSearch.SearchParameters parameters) {
        if (parameters.searchType == OldSearch.SearchType.FIELD || parameters.searchType == OldSearch.SearchType.CARPET_BOMB) {
            if (parameters.mappings == INTERMEDIARY || parameters.mappings == ALL) {
                for (String item : intermediary.values()) {
                    if (oldFilter(item, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_CLIENT || parameters.mappings == ALL) {
                if (oldFilter(obfuscatedClient, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_SERVER || parameters.mappings == ALL) {
                if (oldFilter(obfuscatedServer, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == BABRIC || parameters.mappings == ALL) {
                for (String item : babric.values()) {
                    if (oldFilter(item, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_CLIENT || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, String> item : mcp.entrySet()) {
                    if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.CLIENT) {
                        if (oldFilter(item.getValue(), parameters)) {
                            return true;
                        }
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_SERVER || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, String> item : mcp.entrySet()) {
                    if (item.getKey().type == MappingType.MCP && item.getKey().environment == Environment.SERVER) {
                        if (oldFilter(item.getValue(), parameters)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String niceString(boolean noChildren) {

        String offset = noChildren ? "" : "      ";

        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("\n" + offset + "FIELD");

        // Obfuscated
        if (!obfuscatedClient.isEmpty()) {
            sb.append("\n" + offset + "Obfuscated Client : ").append(obfuscatedClient);
        }

        if (!obfuscatedServer.isEmpty()) {
            sb.append("\n" + offset + "Obfuscated Server : " + obfuscatedServer);
        }

        // Intermediary
        sb.append("\n" + offset + "Intermediary : ");
        for (var mapping : intermediary.entrySet()) {
            sb.append(mapping.getKey().name + "=" + mapping.getValue() + "  ");
        }

        // Named
        sb.append("\n" + offset + "Mappings : ");
        for (var mapping : mcp.entrySet()) {
            sb.append("\n  " + offset);
            sb.append(mapping.getKey().name + ": " + mapping.getValue() + "  ");
        }

        for (var mapping : babric.entrySet()) {
            sb.append("\n  " + offset);
            sb.append(mapping.getKey().name + ": " + mapping.getValue() + "  ");
        }

        sb.append("\n");

        return sb.toString();
    }
}
