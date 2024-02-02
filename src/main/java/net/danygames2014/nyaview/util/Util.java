package net.danygames2014.nyaview.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static Path getMappingPath(String filename){
        return Paths.get(System.getProperty("user.dir") + "/mappings/" + filename);
    }
}
