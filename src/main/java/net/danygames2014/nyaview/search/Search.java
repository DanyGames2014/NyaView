package net.danygames2014.nyaview.search;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;

import static net.danygames2014.nyaview.search.DisplayParameters.ClassDisplay;
import static net.danygames2014.nyaview.search.DisplayParameters.Verbosity;
import static net.danygames2014.nyaview.search.SearchParameters.*;

public class Search {
    // Current Parameter Defaults
    public static MatchType defaultMatch = MatchType.FUZZY;
    public static boolean defaultCaseSensitive = false;
    public static SearchType defaultType = SearchType.CARPET_BOMB;
    public static SearchMappings defaultMappings = SearchMappings.ALL;

    // Current Display Parameters
    public static ClassDisplay classDisplay = ClassDisplay.FULL;
    public static Verbosity classVerbosity = Verbosity.FULL;
    public static Verbosity methodVerbosity = Verbosity.FULL;
    public static Verbosity fieldVerbosity = Verbosity.FULL;

    public static SearchResult search(String searchQuery) {
        return search(SearchParameters.parse(searchQuery));
    }

    public static SearchResult search(SearchParameters parameters) {
        SearchResult result = new SearchResult();

        // Loop thru all the classes
        for (ClassMappingEntry classEntry : NyaView.loader.classes) {
            // Check the class itself
            if (parameters.type == SearchType.CARPET_BOMB || parameters.type == SearchType.CLASS) {
                if (classEntry.match(parameters)) {
                    result.add(classEntry);
                }
            }

            // Check the methods in the class
            if (parameters.type == SearchType.CARPET_BOMB || parameters.type == SearchType.METHOD) {
                for (var methodEntry : classEntry.methods) {
                    if (methodEntry.match(parameters)) {
                        result.add(methodEntry);
                    }
                }
            }

            // Check the fields in the class
            if (parameters.type == SearchType.CARPET_BOMB || parameters.type == SearchType.FIELD) {
                for (var fieldEntry : classEntry.fields) {
                    if (fieldEntry.match(parameters)) {
                        result.add(fieldEntry);
                    }
                }
            }
        }

        return result;
    }

    public static void printResult(SearchResult result, SearchParameters searchParameters, DisplayParameters displayParameters) {
        System.out.println(searchParameters);

        for (var r : result.results.entrySet()) {
            if (r.getValue().classMatch) {
                System.out.println(r.getKey().searchString(displayParameters.classDisplay, displayParameters.classVerbosity, displayParameters.methodVerbosity, displayParameters.fieldVerbosity));
            } else {
                if (!r.getValue().methods.isEmpty() || !r.getValue().fields.isEmpty()) {
                    switch (displayParameters.classDisplay) {
                        case ONLY_METHODS, ONLY_FIELDS, MINIMAL -> {
                            System.out.println(r.getKey().searchString(ClassDisplay.MINIMAL, displayParameters.classVerbosity, displayParameters.methodVerbosity, displayParameters.fieldVerbosity));

                            // Methods
                            if (!r.getValue().methods.isEmpty()) {
                                System.out.print("\n   Methods :");
                                for (var m : r.getValue().methods) {
                                    System.out.println(m.searchString(displayParameters.methodVerbosity, true));
                                }
                            }

                            // Fields
                            if (!r.getValue().fields.isEmpty()) {
                                System.out.print("\n   Fields :");
                                for (var f : r.getValue().fields) {
                                    System.out.println(f.searchString(displayParameters.fieldVerbosity, true));
                                }
                            }

                        }
                        
                        case NONE -> {
                            // Methods
                            for (var m : r.getValue().methods) {
                                System.out.println(m.searchString(displayParameters.methodVerbosity, false));
                            }

                            // Fields
                            for (var f : r.getValue().fields) {
                                System.out.println(f.searchString(displayParameters.fieldVerbosity, false));
                            }
                        }
                        
                        default -> {
                            System.out.println(r.getKey().searchString(displayParameters.classDisplay, displayParameters.classVerbosity, displayParameters.methodVerbosity, displayParameters.fieldVerbosity));
                        }

                    }
                }
            }

        }

        System.out.println("Found " + result.methodCount + " methods and " + result.fieldCount + " fields in " + result.results.size() + (result.results.size() > 1 ? " classes" : " class"));
    }
}
