package net.danygames2014.nyaview.mapping;

import blue.endless.jankson.Comment;
import net.danygames2014.nyaview.NyaView;
import net.fabricmc.mappingio.format.MappingFormat;

public class Mappings {
    @Comment("Identifier of the mappings")
    public String id;

    @Comment("Human readable name")
    public String name;

    @Comment("Mapping Type")
    public MappingType type;

    @Comment("Path to the mappings in the mappings directory")
    public String path;

    @Comment("Maping Format")
    public MappingFormat format;

    @Comment("Parent Mapping ID (used for Intermediary based Fabric mappings)")
    public String parentId;


    public Mappings(String id, String name, MappingType type, String path, MappingFormat format) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.path = path;
        this.format = format;
    }

    public Mappings() {
    }

    public Mappings getParent() {
        if (this.type != MappingType.BABRIC) {
            return null;
        }

        return NyaView.config.getMappings(this.parentId);
    }

    @Override
    public String toString() {
        return name;
    }

    public String toUglyString() {
        return "Mappings{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", path='" + path + '\'' +
                ", format=" + format +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
