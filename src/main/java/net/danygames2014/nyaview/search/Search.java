package net.danygames2014.nyaview.search;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;

import static net.danygames2014.nyaview.search.DisplayParameters.*;
import static net.danygames2014.nyaview.search.SearchParameters.*;

public class Search {
    // Current Parameter Defaults
    public MatchType match = MatchType.FUZZY;
    public boolean caseSensitive = false;
    public SearchType type = SearchType.CARPET_BOMB;
    public SearchMappings mappings = SearchMappings.ALL;

    // Current Display Parameters
    public ClassDisplay classDisplay = ClassDisplay.FULL;
    public Verbosity classVerbosity = Verbosity.FULL;
    public Verbosity methodVerbosity = Verbosity.FULL;
    public Verbosity fieldVerbosity = Verbosity.FULL;
    
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
}
