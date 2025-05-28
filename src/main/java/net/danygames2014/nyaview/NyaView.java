package net.danygames2014.nyaview;

import net.danygames2014.nyaview.log.Logger;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.Mappings;
import org.simpleyaml.configuration.serialization.ConfigurationSerialization;

public class NyaView {
    public static final Logger LOGGER = new Logger("NyaView", Logger.LogLevel.INFO);
    public static Config config;
    public static MappingLoader loader;
    
    public static void init() {
        ConfigurationSerialization.registerClass(Intermediary.class);
        ConfigurationSerialization.registerClass(Mappings.class);

        config = new Config("config.yml");
        loader = new MappingLoader();

        for (Intermediary intermediary : config.getIntermediaryList()) {
            loader.load(intermediary);
        }
        
        for (Mappings mappingSet : config.getMappingList()) {
            loader.load(mappingSet);
        }
    }
}
