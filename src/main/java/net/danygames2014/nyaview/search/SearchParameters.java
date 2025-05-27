package net.danygames2014.nyaview.search;

import net.danygames2014.nyaview.NyaView;

public class SearchParameters {
    public String query;
    public SearchType type;
    public MatchType match;
    public boolean caseSensitive;
    public SearchMappings mappings;

    // This is actually a Display Parameter flag and has no effect on search
    public boolean noChildren;

    public SearchParameters() {
    }

    public static SearchParameters parse(String query) {
        // Create the parameters
        SearchParameters params = new SearchParameters();

        // Write the default values
        params.type = Search.defaultType;
        params.match = Search.defaultMatch;
        params.caseSensitive = Search.defaultCaseSensitive;
        params.mappings = Search.defaultMappings;
        params.noChildren = false;
        params.query = query;

        // Parse the Search Query
        // Parse the divider which specifies the match type and some display parameters
        String splitDivider = parseAndReturnDivider(query, params);

        String[] split;

        // If the split divider is not blank, split the query
        if (!splitDivider.isBlank()) {
            // Split the query at the divider.
            // String at index 0 will be stuff like search type parameter and mappings
            // String at index 1 will be stuff like the query and case sensitivity indicator
            split = query.split(splitDivider);

            // If for some reason the query was not split even tho there was a divider, return
            if (split.length != 2) {
                NyaView.LOGGER.warn("There was a split divider, but the query [" + query + "] was not split into 2 parts. It was split into " + split.length + " instead.");
                split = new String[]{"", query};
            }
        } else {
            split = new String[]{"", query};
        }

        // Whatever happens, the query is the string at index 1
        params.query = split[1];

        // If the first character of the query is a #, we want the search to be case sensitive and remove the # from the query
        if (!params.query.isEmpty() && params.query.charAt(0) == '#') {
            params.caseSensitive = true;
            params.query = params.query.substring(1);
        }

        // Parse the search type
        for (SearchType searchType : SearchType.values()) {
            if (!split[0].isEmpty() && split[0].charAt(0) == searchType.searchChar) {
                params.type = searchType;
                // Consume the character so we can properly match mappings type
                split[0] = split[0].substring(1);
                break;
            }
        }

        // Parse the mappings type
        for (SearchMappings mappingType : SearchMappings.values()) {
            if (split[0].contains(mappingType.searchString)) {
                params.mappings = mappingType;
            }
        }

        // Return the parameters
        return params;
    }

    public static String parseAndReturnDivider(String query, SearchParameters params) {
        if (query.contains("!!")) {
            params.match = MatchType.STRICT;
            params.noChildren = true; // DisplayParam for Class Display
            return "[!][!]";
        } else if (query.contains("?!")) {
            params.match = MatchType.FUZZY;
            params.noChildren = true; // DisplayParam for Class Display
            return "[?][!]";
        } else if (query.contains("!")) {
            params.match = MatchType.STRICT;
            return "[!]";
        } else if (query.contains("?")) {
            params.match = MatchType.FUZZY;
            return "[?]";
        }

        return "";
    }

    @Override
    public String toString() {
        return "SearchParameters:" +
                "\n  Search Type = " + type +
                "\n  Match Type = " + match +
                "\n  Mappings = " + mappings +
                "\n  Case Sensitive = " + (caseSensitive ? "Yes" : "No") +
                "\n  Query = " + query +
                (noChildren ? "\n  No Children" : "");
    }

    public enum MatchType {
        // It is enough that part of the query matches
        FUZZY,

        // The full query needs to match
        STRICT
    }

    public enum SearchType {
        // Carpet bombing go brrr, search everything, drop the bombs hell yeah girlll!!!
        CARPET_BOMB('a'),

        // Searches only class names
        CLASS('c'),

        // Searches only method names
        METHOD('m'),

        // Search only field names
        FIELD('f');

        public final char searchChar;

        SearchType(char searchChar) {
            this.searchChar = searchChar;
        }
    }

    public enum SearchMappings {
        OBFUSCATED("o"),
        OBFUSCATED_CLIENT("oc"),
        OBFUSCATED_SERVER("os"),
        MCP("m"),
        MCP_CLIENT("mc"),
        MCP_SERVER("ms"),
        INTERMEDIARY("i"),
        FABRIC("f"),
        ALL("a");

        public final String searchString;

        SearchMappings(String searchString) {
            this.searchString = searchString;
        }
    }
}
