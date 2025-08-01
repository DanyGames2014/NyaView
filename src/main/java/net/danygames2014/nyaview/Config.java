package net.danygames2014.nyaview;

import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.Mappings;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Config {
    private final YamlFile yamlFile;

    public Config(String filename) {
        yamlFile = new YamlFile(filename);

        try {
            if (!yamlFile.exists()) {
                yamlFile.createNewFile();
                NyaView.LOGGER.info("Created new config file: " + yamlFile.getFilePath());
            } else {
                NyaView.LOGGER.info("Config file found: " + yamlFile.getFilePath() + ". Loading...");
            }

            yamlFile.loadWithComments();
            init();
            yamlFile.loadWithComments();
        } catch (Exception e) {
            NyaView.LOGGER.error("Failed to load config file: " + filename, e);
        }
    }

    public void init() {
        yamlFile.setHeader("--- NyaView Mappings Configuration File ---");

        // Profile
        yamlFile.addDefault("activeProfile", "default");

        save();
    }

    public String getActiveProfileId() {
        return yamlFile.getString("activeProfile");
    }
    
    @Deprecated(forRemoval = true)
    public String getDownloadVersion() {
        return NyaView.profileManager.activeProfile.getVersion();
    }

    @Deprecated(forRemoval = true)
    public Intermediary getIntermediary(String id) {
        return NyaView.profileManager.activeProfile.getIntermediary(id);
    }

    @Deprecated(forRemoval = true)
    public Mappings getMappings(String id) {
        return NyaView.profileManager.activeProfile.getMappings(id);
    }

    @Deprecated(forRemoval = true)
    public ArrayList<Intermediary> getIntermediaryList() {
        return NyaView.profileManager.activeProfile.getIntermediaryList();
    }

    @Deprecated(forRemoval = true)
    public ArrayList<Mappings> getMappingList() {
        return NyaView.profileManager.activeProfile.getMappingList();
    }

    @Deprecated(forRemoval = true)
    public ActionResult addMappings(String key, Mappings mapping) {
        return NyaView.profileManager.activeProfile.addMappings(key, mapping);
    }

    @Deprecated(forRemoval = true)
    public ActionResult addIntermediaries(String key, Intermediary intermediary) {
        return NyaView.profileManager.activeProfile.addIntermediaries(key, intermediary);
    }

    @Deprecated(forRemoval = true)
    public ActionResult removeMappings(String key) {
        return NyaView.profileManager.activeProfile.removeMappings(key);
    }

    @Deprecated(forRemoval = true)
    public ActionResult removeIntermediaries(String key) {
        return NyaView.profileManager.activeProfile.removeIntermediaries(key);
    }

    @Deprecated(forRemoval = true)
    public ArrayList<String> getIgnoredPackages() {
        return NyaView.profileManager.activeProfile.getIgnoredPackages();
    }

    public YamlFile getYamlFile() {
        if (yamlFile == null) {
            var exception = new IllegalStateException("Config file not loaded yet.");
            NyaView.LOGGER.error("Config file not loaded yet.", exception);
            throw exception;
        }

        return yamlFile;
    }

    public boolean save() {
        return save(false);
    }

    public boolean save(boolean writeMappingLists) {
        try {
            if (writeMappingLists) {
                yamlFile.set("intermediaries", getIntermediaryList());
                yamlFile.set("mappings", getMappingList());
            }
            yamlFile.save();
        } catch (IOException e) {
            NyaView.LOGGER.error("Failed to save config file: " + yamlFile.getFilePath(), e);
            return false;
        }
        return true;
    }
}
