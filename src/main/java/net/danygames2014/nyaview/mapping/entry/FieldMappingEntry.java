package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;

import java.util.HashMap;

public class FieldMappingEntry implements MappingEntry {
    public ClassMappingEntry parent;


    // Obfuscated
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP
    public String mcp;

    // Intermediary
    public String intermediary;

    // Babric
    public HashMap<Mappings, String> babric;


    @Override
    public String getObfuscatedClientName() {
        return obfuscatedClient;
    }

    @Override
    public String getObfuscatedServerName() {
        return obfuscatedServer;
    }

    @Override
    public String getMcpName() {
        return mcp;
    }

    @Override
    public String getIntermediaryName() {
        return intermediary;
    }

    @Override
    public String getBabricName(Mappings mappings) {
        return babric.get(mappings);
    }
}
