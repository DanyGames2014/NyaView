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
    private final HashMap<String, Mappings> mappings;
    private final HashMap<String, Intermediary> intermediaries;
    private ArrayList<String> ignoredPackages;

    public Config(String filename) {
        yamlFile = new YamlFile(filename);
        mappings = new HashMap<>();
        intermediaries = new HashMap<>();
        ignoredPackages = new ArrayList<>();

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

            for (Object o : yamlFile.getList("intermediaries")) {
                if (o instanceof Intermediary intermediary) {
                    intermediaries.putIfAbsent(intermediary.id, intermediary);
                }
            }

            for (Object o : yamlFile.getList("mappings")) {
                if (o instanceof Mappings mappingSet) {
                    mappings.putIfAbsent(mappingSet.id, mappingSet);
                }
            }

            //noinspection unchecked
            ignoredPackages = (ArrayList<String>) yamlFile.getList("ignoredPackages", ignoredPackages);

        } catch (Exception e) {
            NyaView.LOGGER.error("Failed to load config file: " + filename, e);
        }
    }

    public void init() {
        yamlFile.setHeader("--- NyaView Mappings Configuration File ---");

        // Download Version
        yamlFile.addDefault("downloadVersion", "b1.7.3");
        yamlFile.setComment("downloadVersion", "Not Yet Implemented");

        // Ignored Packages
        ArrayList<String> ignoredPackages = new ArrayList<>();
//        ignoredPackages.add("argo");
        yamlFile.addDefault("ignoredPackages", ignoredPackages);

        // Intermediaries
        ArrayList<Intermediary> exampleIntermediaries = new ArrayList<>();
        yamlFile.addDefault("intermediaries", exampleIntermediaries);

        // Mappings
        ArrayList<Mappings> exampleMappings = new ArrayList<>();
        yamlFile.addDefault("mappings", exampleMappings);

        save();
    }

    public String getDownloadVersion() {
        return yamlFile.getString("downloadVersion");
    }

    public Intermediary getIntermediary(String id) {
        return intermediaries.getOrDefault(id, null);
    }

    public Mappings getMappings(String id) {
        return mappings.getOrDefault(id, null);
    }

    public ArrayList<Intermediary> getIntermediaryList() {
        return new ArrayList<>(intermediaries.values());
    }

    public ArrayList<Mappings> getMappingList() {
        return new ArrayList<>(mappings.values());
    }

    public ActionResult addMappings(String key, Mappings mapping) {
        if (!mappings.containsKey(key)) {
            mappings.put(key, mapping);
            if (!save(true)) {
                return new ActionResult(10, "Error while saving config file");
            }
            return new ActionResult(0, "Mappings " + key + " added successfully");
        }
        return new ActionResult(11, "Mappings " + key + " already exist");
    }

    public ActionResult addIntermediaries(String key, Intermediary intermediary) {
        if (!intermediaries.containsKey(key)) {
            intermediaries.put(key, intermediary);
            if (!save(true)) {
                return new ActionResult(10, "Error while saving config file");
            }
            return new ActionResult(0, "Intermediaries " + key + " added successfully");
        }
        return new ActionResult(12, "Intermediaries " + key + " already exist");
    }

    public ActionResult removeMappings(String key) {
        if (mappings.containsKey(key)) {
            mappings.remove(key);
            if (!save(true)) {
                return new ActionResult(10, "Error while saving config file");
            }
            return new ActionResult(0, "Mappings " + key + " removed successfully");
        }
        return new ActionResult(13, "Mappings " + key + " not found");
    }

    public ActionResult removeIntermediaries(String key) {
        if (intermediaries.containsKey(key)) {
            intermediaries.remove(key);
            if (!save(true)) {
                return new ActionResult(10, "Error while saving config file");
            }
            return new ActionResult(0, "Intermediaries " + key + " removed successfully");
        }
        return new ActionResult(14, "Intermediaries " + key + " not found");
    }

    public ArrayList<String> getIgnoredPackages() {
        return ignoredPackages;
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
