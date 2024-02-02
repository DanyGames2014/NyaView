package net.danygames2014.nyaview.mapping;

import net.danygames2014.nyaview.util.Environment;
import net.fabricmc.mappingio.format.MappingFormat;

import java.nio.file.Path;

public class Mappings {
    public String name;
    public String visibleName;
    public MappingType type;
    public Path path;
    public MappingFormat format;

    public Mappings(String name, String visibleName, MappingType type, Path path, MappingFormat format) {
        this.name = name;
        this.visibleName = visibleName;
        this.type = type;
        this.path = path;
        this.format = format;
    }
}