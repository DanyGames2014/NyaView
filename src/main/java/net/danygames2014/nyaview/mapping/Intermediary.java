package net.danygames2014.nyaview.mapping;

import net.danygames2014.nyaview.NyaView;
import net.fabricmc.mappingio.format.MappingFormat;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Intermediary")
public class Intermediary implements ConfigurationSerializable {
    // Identifier of the mappings
    public final String id;

    // Human readable name
    public final String name;

    // Type of the mappings
    public final Environment environment;
    
    // The name of intermediary in the mapping file
    public final String intermediaryName;
    
    // The name of the client mapping in the mapping file
    public final String clientName;

    // The name of the server mapping in the mapping file
    public final String serverName;

    // Path to the mappings in the mappings directory
    public final String path;

    // Format of the mappings
    public MappingFormat format;

    public Intermediary(String id, String name, Environment environment, String intermediaryName, String clientName, String serverName, String path, MappingFormat format) {
        this.id = id;
        this.name = name;
        this.environment = environment;
        this.intermediaryName = intermediaryName;
        this.clientName = clientName;
        this.serverName = serverName;
        this.path = path;
        this.format = format;
    }

    @Override
    public String toString() {
        return name;
    }

    // Serialization
    public static Intermediary deserialize(final Map<String, Object> mappedObject) {
        return new Intermediary(
                (String) mappedObject.get("id"),
                (String) mappedObject.get("name"),
                Environment.valueOf((String) mappedObject.get("environment")),
                (String) mappedObject.get("intermediaryName"),
                (String) mappedObject.get("clientName"),
                (String) mappedObject.get("serverName"),
                (String) mappedObject.get("path"),
                MappingFormat.valueOf((String) mappedObject.get("format"))
        );
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> mappedObject = new LinkedHashMap<>();
        mappedObject.put("id", id);
        mappedObject.put("name", name);
        mappedObject.put("environment", environment.name());
        mappedObject.put("intermediaryName", intermediaryName);
        mappedObject.put("clientName", clientName);
        mappedObject.put("serverName", serverName);
        mappedObject.put("path", path);
        mappedObject.put("format", format.name());
        return mappedObject;
    }
}
