package net.danygames2014.nyaview.mapping;

import net.fabricmc.mappingio.format.MappingFormat;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Mappings")
public class Mappings implements ConfigurationSerializable {
    // Identifier of the mappings
    public String id;

    // Human readable name
    public String name;

    // The environment of the mappings
    public Environment environment;
    
    // Type of the mappings
    public MappingType type;

    // Path to the mappings in the mappings directory
    public String path;

    // Format of the mappings
    public MappingFormat format;

    // ID of the marrent mappings for mappings based on intermediaries
    public String intermediaryId;

    public Mappings(String id, String name, Environment environment, MappingType type, String path, MappingFormat format, String intermediaryId) {
        this.id = id;
        this.name = name;
        this.environment = environment;
        this.type = type;
        this.path = path;
        this.format = format;
        this.intermediaryId = intermediaryId;
    }

    public Mappings(String id, String name, Environment environment, MappingType type, String path, MappingFormat format) {
        this(id, name, environment, type, path, format, null);
    }

    private Mappings() {
    }

    @Override
    public String toString() {
        return name;
    }

    public String toUglyString() {
        return "Mappings{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", environment=" + environment +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", format=" + format +
                ", intermediaryId='" + intermediaryId + '\'' +
                '}';
    }

    // Serialization
    public static Mappings deserialize(final Map<String, Object> mappedObject) {
        return new Mappings(
                (String) mappedObject.get("id"),
                (String) mappedObject.get("name"),
                Environment.valueOf((String) mappedObject.get("environment")),
                MappingType.valueOf((String) mappedObject.get("type")),
                (String) mappedObject.get("path"),
                MappingFormat.valueOf((String) mappedObject.get("format")),
                (String) mappedObject.get("intermediaryId")
        );
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> mappedObject = new LinkedHashMap<>();
        mappedObject.put("id", id);
        mappedObject.put("name", name);
        mappedObject.put("environment", environment.name());
        mappedObject.put("type", type.name());
        mappedObject.put("path", path);
        mappedObject.put("format", format.name());
        mappedObject.put("intermediaryId", intermediaryId);
        return mappedObject;
    }
}
