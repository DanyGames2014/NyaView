package net.danygames2014.nyaview;

import net.danygames2014.nyaview.download.DownloadCatalog;
import net.danygames2014.nyaview.log.Logger;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingLoader;
import net.danygames2014.nyaview.mapping.Mappings;
import net.danygames2014.nyaview.profile.ProfileManager;
import org.simpleyaml.configuration.serialization.ConfigurationSerialization;

public class NyaView {
    public static final Logger LOGGER = new Logger("NyaView", Logger.LogLevel.INFO);
    public static Config config;
    public static MappingLoader loader;
    public static DownloadCatalog downloadCatalog;
    public static ProfileManager profileManager;

    public static void init() {
        ConfigurationSerialization.registerClass(Intermediary.class);
        ConfigurationSerialization.registerClass(Mappings.class);

        config = new Config("config.yml");
        profileManager = new ProfileManager();
        profileManager.init();
        downloadCatalog = new DownloadCatalog();
        
        if (loader == null) {
            loadMappings();
        }
    }
    
    public static void loadMappings() {
        loader = new MappingLoader();

        for (Intermediary intermediary : profileManager.activeProfile.getIntermediaryList()) {
            loader.load(intermediary);
        }

        for (Mappings mappingSet : profileManager.activeProfile.getMappingList()) {
            loader.load(mappingSet);
        }
    }
}
