package net.danygames2014.nyaview.util;

import net.danygames2014.nyaview.mapping.entry.ClassPath;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static Path getMappingPath(String filename){
        return Paths.get(System.getProperty("user.dir") + "/mappings/" + filename);
    }

    public static boolean filter(String input, Search.SearchParameters parameters){
        switch (parameters.filterType){
            case FUZZY -> {
                return input.toLowerCase().contains(parameters.query);
            }
            case STRICT, SUPERSTRICT -> {
                return input.toLowerCase().equals(parameters.query);
            }
        }
        return false;
    }

    public static String inputOrEmpty(String input){
        if(input == null){
            return "";
        }
        return input;
    }
}
