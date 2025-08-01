package net.danygames2014.nyaview.profile;

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

    public ArrayList<Intermediary> getIntermediaryList() {
        return new ArrayList<>(intermediaries.values());
    }

    public ArrayList<Mappings> getMappingList() {
        return new ArrayList<>(mappings.values());
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
