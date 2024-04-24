package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;
import net.danygames2014.nyaview.util.Search;
import net.danygames2014.nyaview.util.Searchable;
import net.danygames2014.nyaview.util.Util;

import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.util.Search.SearchMappings.*;
import static net.danygames2014.nyaview.util.Util.filter;

public class FieldMappingEntry implements Searchable {
    // Obfuscated
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP
    public HashMap<Mappings, String> mcp;

    // Intermediary
    public HashMap<Mappings, String> intermediary;

    // Babric
    public HashMap<Mappings, String> babric;

    public FieldMappingEntry() {
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
        babric = new HashMap<>();
    }

    // MCP
    public String getMcpName(Mappings mappings){
        return mcp.get(mappings);
    }

    public String getMcpName(String id){
        return mcp.get(NyaView.config.getMappings(id));
    }

    // Intermediary
    public String getIntermediaryName(Mappings mappings){
        return intermediary.get(mappings);
    }

    public String getIntermediaryName(String id){
        return intermediary.get(NyaView.config.getMappings(id));
    }

    // Fabric
    public String getBabricName(Mappings mappings){
        return babric.get(mappings);
    }

    public String getBabricName(String id){
        return babric.get(NyaView.config.getMappings(id));
    }

    @Override
    public String toString() {
        return
                "\n      FIELD" +
                "\n         obfuscatedClient = " + obfuscatedClient +
                "\n         obfuscatedServer = " + obfuscatedServer +
                "\n         mcp = " + mcp +
                "\n         intermediary = " + intermediary +
                "\n         babric = " + babric + "\n";
    }

    @Override
    public boolean match(Search.SearchParameters parameters) {
        if(parameters.searchType == Search.SearchType.FIELD || parameters.searchType == Search.SearchType.CARPET_BOMB){
            if(parameters.mappings == INTERMEDIARY || parameters.mappings == ALL){
                for (String item : intermediary.values()){
                    if(filter(item, parameters)){
                        return true;
                    }
                }
            }
            if(parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_CLIENT || parameters.mappings == ALL){
                if(filter(obfuscatedClient, parameters)){
                    return true;
                }
            }
            if(parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_SERVER || parameters.mappings == ALL){
                if(filter(obfuscatedServer, parameters)){
                    return true;
                }
            }
            if(parameters.mappings == BABRIC || parameters.mappings == ALL){
                for (String item : babric.values()){
                    if(filter(item, parameters)){
                        return true;
                    }
                }
            }
            if(parameters.mappings == MCP || parameters.mappings == MCP_CLIENT || parameters.mappings == ALL){
                for (Map.Entry<Mappings, String> item : mcp.entrySet()){
                    if(item.getKey().type == MappingType.MCP_CLIENT){
                        if(filter(item.getValue(), parameters)){
                            return true;
                        }
                    }
                }
            }
            if(parameters.mappings == MCP || parameters.mappings == MCP_SERVER || parameters.mappings == ALL){
                for (Map.Entry<Mappings, String> item : mcp.entrySet()){
                    if(item.getKey().type == MappingType.MCP_SERVER){
                        if(filter(item.getValue(), parameters)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String niceString(boolean superstrict) {

        String offset = superstrict ? "" : "      ";

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
