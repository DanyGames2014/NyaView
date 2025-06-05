package net.danygames2014.nyaview;

import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.Mappings;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
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
        if (!yamlFile.contains("intermediaries")) {
            ArrayList<Intermediary> exampleIntermediaries = new ArrayList<>();

//            exampleIntermediaries.add(new Intermediary("babric", "Babric", Environment.MERGED, "intermediary", "client", "server", "babric.tiny", MappingFormat.TINY_2_FILE));
//            exampleIntermediaries.add(new Intermediary("calamus_1_client", "Calamus Gen 1 Client", Environment.CLIENT, "intermediary", "official", null, "calamus_gen1_client.tiny", MappingFormat.TINY_2_FILE));
//            exampleIntermediaries.add(new Intermediary("calamus_2", "Calamus Gen 2", Environment.MERGED, "intermediary", "clientOfficial", "serverOfficial", "calamus_gen2.tiny", MappingFormat.TINY_2_FILE));

            yamlFile.addDefault("intermediaries", exampleIntermediaries);
        }

        // Mappings
        if (!yamlFile.contains("mappings")) {
            ArrayList<Mappings> exampleMappings = new ArrayList<>();

//            exampleMappings.add(new Mappings("biny", "BINY", Environment.MERGED, MappingType.BABRIC, "biny", MappingFormat.ENIGMA_DIR, "babric"));

            yamlFile.addDefault("mappings", exampleMappings);
        }

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

    public boolean addMappings(String key, Mappings mapping) {
        if (!mappings.containsKey(key)) {
            mappings.put(key, mapping);
            save(true);
            return true;
        }
        return false;
    }

    public boolean addIntermediaries(String key, Intermediary intermediary) {
        if (!intermediaries.containsKey(key)) {
            intermediaries.put(key, intermediary);
            save(true);
            return true;
        }
        return false;
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
    
    @SuppressWarnings("UnusedReturnValue")
    public boolean save(boolean setMappingLists) {
        try {
            if(setMappingLists) {
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
