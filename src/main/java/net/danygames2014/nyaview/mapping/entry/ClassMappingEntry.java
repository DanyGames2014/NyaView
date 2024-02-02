package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;

import java.util.HashMap;

@SuppressWarnings("unused")
public class ClassMappingEntry implements MappingEntry {
    public Environment environment;

    // Obfuscated
    public String obfuscatedClient;
    public String obfuscatedServer;

    // MCP
    public String mcp;

    // Intermediary
    public String intermediaryPackage;
    public String intermediary;

    // Babric
    public HashMap<Mappings, String> babric;

    public ClassMappingEntry() {
        babric = new HashMap<>();
    }

    public Environment getEnvironment() {
        return environment;
    }

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

    public String getIntermediaryPackage() {
        return intermediaryPackage;
    }

    @Override
    public String getBabricName(Mappings mappings) {
        String babricName = babric.get(mappings);
        if(babricName != null){
            String[] tempName = babricName.split("/");
            babricName = tempName[tempName.length-1];
        }
        return babricName;
    }

    @Override
    public String toString() {
        return environment + " | " + mcp + " | " + obfuscatedClient + " | " + obfuscatedServer + " | " + intermediary + " | " + babric;
    }
}