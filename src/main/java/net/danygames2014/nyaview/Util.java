package net.danygames2014.nyaview;

import net.danygames2014.nyaview.search.OldSearch;
import net.danygames2014.nyaview.search.SearchParameters;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {
    public static Path getMappingPath(String filename) {
        return Paths.get(System.getProperty("user.dir") + "/mappings/" + filename);
    }

    public static String inputOrEmpty(String input) {
        if (input == null) {
            return "";
        }
        return input;
    }

    public static boolean filter(String input, SearchParameters parameters) {
        switch (parameters.match) {
            case FUZZY -> {
                if (parameters.caseSensitive) {
                    return input.contains(parameters.query);
                } else {
                    return input.toLowerCase().contains(parameters.query.toLowerCase());
                }
            }
            case STRICT -> {
                if (parameters.caseSensitive) {
                    return input.equals(parameters.query);
                } else {
                    return input.equalsIgnoreCase(parameters.query);
                }
            }
        }
        return false;
    }

    public static boolean oldFilter(String input, OldSearch.SearchParameters parameters) {
        switch (parameters.filterType) {
            case FUZZY -> {
                return input.toLowerCase().contains(parameters.query);
            }
            case STRICT, SUPERSTRICT -> {
                return input.toLowerCase().equals(parameters.query);
            }
        }
        return false;
    }
}
