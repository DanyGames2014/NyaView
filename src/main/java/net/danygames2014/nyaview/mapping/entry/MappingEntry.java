package net.danygames2014.nyaview.mapping.entry;

import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.util.Environment;

public interface MappingEntry {
    String getObfuscatedClientName();
    String getObfuscatedServerName();
    String getMcpName();
    String getIntermediaryName();
    String getBabricName(Mappings mappings);
}
