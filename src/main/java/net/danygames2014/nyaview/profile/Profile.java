package net.danygames2014.nyaview.profile;

import net.danygames2014.nyaview.ActionResult;
import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.Mappings;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Profile {
    private final YamlFile yamlFile;
    private final HashMap<String, Mappings> mappings;
    private final HashMap<String, Intermediary> intermediaries;
    private ArrayList<String> ignoredPackages;

    public Profile(String path, String id, String name, String version) {
        this(path);
        this.yamlFile.set("id", id);
        this.yamlFile.set("name", name);
        this.yamlFile.set("version", version);
    }
    
    public Profile(String path) {
        yamlFile = new YamlFile(path);
        mappings = new HashMap<>();
        intermediaries = new HashMap<>();
        ignoredPackages = new ArrayList<>();
        
        try {
            if (!yamlFile.exists()) {
                yamlFile.createNewFile();
                NyaView.LOGGER.info("Created new profile file: " + yamlFile.getFilePath());
            } else {
                NyaView.LOGGER.info("Profile file found: " + yamlFile.getFilePath() + ". Loading...");
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
            NyaView.LOGGER.error("Failed to load profile file: " + path, e);
        }
    }
    
    public void init() {
        yamlFile.setHeader("--- NyaView Profile Configuration File ---");
        
        // ID
        yamlFile.addDefault("id", "default");
        
        // Name
        yamlFile.addDefault("name", "Default Profile");
        
        // Version
        yamlFile.addDefault("version", "b1.7.3");

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

    // ID
    public String getId() {
        return yamlFile.getString("id");
    }
    
    // Name
    public String getName() {
        return yamlFile.getString("name");
    }
    
    // Version
    public String getVersion() {
        return yamlFile.getString("version");
    }

    // Intermediary
    public Intermediary getIntermediary(String id) {
        return intermediaries.getOrDefault(id, null);
    }

    public ArrayList<Intermediary> getIntermediaryList() {
        return new ArrayList<>(intermediaries.values());
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

    // Mappings
    public Mappings getMappings(String id) {
        return mappings.getOrDefault(id, null);
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

    // Ignored Packages
    public ArrayList<String> getIgnoredPackages() {
        return ignoredPackages;
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
