package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;
import net.danygames2014.nyaview.util.Search;
import net.danygames2014.nyaview.util.Searchable;
import net.danygames2014.nyaview.util.descriptor.Descriptor;

import java.util.HashMap;
import java.util.Map;

import static net.danygames2014.nyaview.util.Search.SearchMappings.*;
import static net.danygames2014.nyaview.util.Util.filter;

@SuppressWarnings("unused")
public class MethodMappingEntry implements Searchable {
    // Environment
    public Environment environment;

    // Obfuscated
    public Method obfuscatedClient;
    public Method obfuscatedServer;

    // Obfuscated Descriptor
//    public String obfuscatedClientDesc;
//    public String obfuscatedServerDesc;

    // MCP
    public HashMap<Mappings, Method> mcp;

    // Intermediary
    public HashMap<Mappings, Method> intermediary;
//    public HashMap<Mappings, String> intermediaryDesc;

    // Babric
    public HashMap<Mappings, Method> babric;

    public MethodMappingEntry() {
        mcp = new HashMap<>();
        intermediary = new HashMap<>();
//        intermediaryDesc = new HashMap<>();
        babric = new HashMap<>();
        obfuscatedClient = new Method();
        obfuscatedServer = new Method();
    }

    // MCP
    public String getMcpName(Mappings mappings) {
        return mcp.get(mappings).name;
    }

    public String getMcpName(String id) {
        return mcp.get(NyaView.config.getMappings(id)).name;
    }

    // Intermediary
    public String getIntermediaryName(Mappings mappings) {
        return intermediary.get(mappings).name;
    }

    public String getIntermediaryName(String id) {
        return intermediary.get(NyaView.config.getMappings(id)).name;
    }

    // Intermediary Descriptor
    public String getIntermediaryDesc(Mappings mappings) {
        return intermediary.get(mappings).desc;
    }

    public String getIntermediaryDesc(String id) {
        return intermediary.get(NyaView.config.getMappings(id)).desc;
    }

    // Fabric
    public String getBabricName(Mappings mappings) {
        return babric.get(mappings).name;
    }

    public String getBabricName(String id) {
        return babric.get(NyaView.config.getMappings(id)).name;
    }

    @Override
    public String toString() {
        return
                "\n      METHOD" +
                        "\n         environment = " + environment +
                        "\n         obfuscatedClient = " + obfuscatedClient +
                        "\n         obfuscatedServer = " + obfuscatedServer +
                        "\n         obfuscatedClientDesc = " + obfuscatedClient.desc +
                        "\n         obfuscatedServerDesc = " + obfuscatedServer.desc +
                        "\n         mcp = " + mcp +
                        "\n         intermediary = " + intermediary +
                        "\n         babric = " + babric + "\n";
    }

    @Override
    public boolean match(Search.SearchParameters parameters) {
        if (parameters.searchType == Search.SearchType.METHOD || parameters.searchType == Search.SearchType.CARPET_BOMB) {
            if (parameters.mappings == INTERMEDIARY || parameters.mappings == ALL) {
                for (Method item : intermediary.values()) {
                    if (filter(item.name, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_CLIENT || parameters.mappings == ALL) {
                if (filter(obfuscatedClient.name, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == OBFUSCATED || parameters.mappings == OBFUSCATED_SERVER || parameters.mappings == ALL) {
                if (filter(obfuscatedServer.name, parameters)) {
                    return true;
                }
            }
            if (parameters.mappings == BABRIC || parameters.mappings == ALL) {
                for (Method item : babric.values()) {
                    if (filter(item.name, parameters)) {
                        return true;
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_CLIENT || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, Method> item : mcp.entrySet()) {
                    if (item.getKey().type == MappingType.MCP_CLIENT) {
                        if (filter(item.getValue().name, parameters)) {
                            return true;
                        }
                    }
                }
            }
            if (parameters.mappings == MCP || parameters.mappings == MCP_SERVER || parameters.mappings == ALL) {
                for (Map.Entry<Mappings, Method> item : mcp.entrySet()) {
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

    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Override
    public String niceString(boolean superstrict) {
        String offset = superstrict ? "" : "      ";

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
