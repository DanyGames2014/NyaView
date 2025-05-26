package net.danygames2014.nyaview;

import net.danygames2014.nyaview.mapping.Environment;
import net.danygames2014.nyaview.mapping.Intermediary;
import net.danygames2014.nyaview.mapping.MappingType;
import net.danygames2014.nyaview.mapping.Mappings;
import net.fabricmc.mappingio.format.MappingFormat;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

        if (!yamlFile.contains("intermediaries")) {
            ArrayList<Intermediary> exampleIntermediaries = new ArrayList<>();

            exampleIntermediaries.add(new Intermediary("babric", "Babric", Environment.MERGED, "intermediary", "client", "server", "babric.tiny", MappingFormat.TINY_2_FILE));
            exampleIntermediaries.add(new Intermediary("calamus_1_client", "Calamus Gen 1 Client", Environment.CLIENT, "intermediary", "official", null, "calamus_gen1_client.tiny", MappingFormat.TINY_2_FILE));
            exampleIntermediaries.add(new Intermediary("calamus_2", "Calamus Gen 2", Environment.MERGED, "intermediary", "clientOfficial", "serverOfficial", "calamus_gen2.tiny", MappingFormat.TINY_2_FILE));
            
            yamlFile.addDefault("intermediaries", exampleIntermediaries);
        }
        
        if (!yamlFile.contains("mappings")) {
            ArrayList<Mappings> exampleMappings = new ArrayList<>();

            exampleMappings.add(new Mappings("biny", "BINY", Environment.MERGED, MappingType.BABRIC, "biny", MappingFormat.ENIGMA_DIR, "babric"));

            yamlFile.addDefault("mappings", exampleMappings);
        }
        
        ArrayList<String> ignoredPackages = new ArrayList<>();
        //ignoredPackages.add("argo");
        yamlFile.addDefault("ignoredPackages", ignoredPackages);

        save();
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

    @SuppressWarnings("UnusedReturnValue")
    public boolean save() {
        try {
            yamlFile.save();
        } catch (IOException e) {
            NyaView.LOGGER.error("Failed to save config file: " + yamlFile.getFilePath(), e);
            return false;
        }
        return true;
    }
}
