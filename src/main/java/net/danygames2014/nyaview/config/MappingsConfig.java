package net.danygames2014.nyaview.config;

import net.danygames2014.nyaview.mapping.Mappings;

import java.util.ArrayList;
import java.util.List;

public class MappingsConfig {
    public List<Mappings> mappings = new ArrayList<>();

    public Mappings getMappings(String id) {
        for (Mappings item : mappings) {
            if (item.id.equals(id)) {
                return item;
            }
        }
        return null;
    }
}
