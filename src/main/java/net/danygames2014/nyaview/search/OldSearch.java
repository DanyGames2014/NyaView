package net.danygames2014.nyaview.search;

import net.danygames2014.nyaview.NyaView;

import java.util.ArrayList;

public class OldSearch {

    @SuppressWarnings("DataFlowIssue")
    public static SearchParameters parseSearchQuery(String searchQuery) {
        String query = searchQuery.strip().toLowerCase();
        SearchParameters parameters = new SearchParameters();

        parameters.mappings = SearchMappings.ALL;
        parameters.searchType = SearchType.CARPET_BOMB;

        String[] queryParts;

        /// Fuzzy Search
        if (query.contains("?")) {
            // Filter Type
            parameters.filterType = FilterType.FUZZY;

            // Split Query
            queryParts = query.split("[?]");

            // Query
            if (queryParts.length > 1 && !queryParts[1].isEmpty()) {
                parameters.query = queryParts[1];
            } else {
                return null;
            }

            // Search Type
            if (!queryParts[0].isEmpty()) {
                if (queryParts[0].charAt(0) == 'c') {
                    parameters.searchType = SearchType.CLASS;
                } else if (queryParts[0].charAt(0) == 'm') {
                    parameters.searchType = SearchType.METHOD;
                } else if (queryParts[0].charAt(0) == 'f') {
                    parameters.searchType = SearchType.FIELD;
                } else {
                    return null;
                }

                // Search Mappings
                if (queryParts[0].length() > 1) {
                    if (queryParts[0].charAt(1) == 'i') {
                        parameters.mappings = SearchMappings.INTERMEDIARY;

                    } else if (queryParts[0].charAt(1) == 'o') {
                        parameters.mappings = SearchMappings.OBFUSCATED;
                        if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 'c') {
                            parameters.mappings = SearchMappings.OBFUSCATED_CLIENT;
                        } else if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 's') {
                            parameters.mappings = SearchMappings.OBFUSCATED_SERVER;
                        }

                    } else if (queryParts[0].charAt(1) == 'b') {
                        parameters.mappings = SearchMappings.BABRIC;

                    } else if (queryParts[0].charAt(1) == 'm') {
                        parameters.mappings = SearchMappings.MCP;
                        if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 'c') {
                            parameters.mappings = SearchMappings.MCP_CLIENT;
                        } else if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 's') {
                            parameters.mappings = SearchMappings.MCP_SERVER;
                        }

                    }
                }
            }

            /// Strict Search
        } else if (query.contains("!")) {
            if (query.contains("!!")) {
                // Filter Type
                parameters.filterType = FilterType.SUPERSTRICT;
                // Split Query
                queryParts = query.split("!!");
            } else {
                // Filter Type
                parameters.filterType = FilterType.STRICT;
                // Split Query
                queryParts = query.split("!");
            }

            // Query
            if (queryParts.length > 1 && !queryParts[1].isEmpty()) {
                parameters.query = queryParts[1].strip().toLowerCase();
            } else {
                return null;
            }

            // Search Type
            if (!queryParts[0].isEmpty()) {
                // Search Type
                if (queryParts[0].charAt(0) == 'c') {
                    parameters.searchType = SearchType.CLASS;
                } else if (queryParts[0].charAt(0) == 'm') {
                    parameters.searchType = SearchType.METHOD;
                } else if (queryParts[0].charAt(0) == 'f') {
                    parameters.searchType = SearchType.FIELD;
                } else {
                    return null;
                }

                // Search Mappings
                if (queryParts[0].length() > 1) {
                    if (queryParts[0].charAt(1) == 'i') {
                        parameters.mappings = SearchMappings.INTERMEDIARY;

                    } else if (queryParts[0].charAt(1) == 'o') {
                        parameters.mappings = SearchMappings.OBFUSCATED;
                        if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 'c') {
                            parameters.mappings = SearchMappings.OBFUSCATED_CLIENT;
                        } else if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 's') {
                            parameters.mappings = SearchMappings.OBFUSCATED_SERVER;
                        }

                    } else if (queryParts[0].charAt(1) == 'b') {
                        parameters.mappings = SearchMappings.BABRIC;

                    } else if (queryParts[0].charAt(1) == 'm') {
                        parameters.mappings = SearchMappings.MCP;
                        if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 'c') {
                            parameters.mappings = SearchMappings.MCP_CLIENT;
                        } else if (queryParts[0].length() > 2 && queryParts[0].charAt(2) == 's') {
                            parameters.mappings = SearchMappings.MCP_SERVER;
                        }

                    }
                }

            }

            /// Carpet Bomb Search
        } else {
            // Filter Type
            parameters.filterType = FilterType.FUZZY;

            // Search Type
            parameters.searchType = SearchType.CARPET_BOMB;

            // Query
            parameters.query = query.strip().toLowerCase();

            // Mappings
            parameters.mappings = SearchMappings.ALL;

            if (query.isEmpty()) {
                return null;
            }
        }

        return parameters;
    }

    public static ArrayList<String> search(SearchParameters parameters) {
        ArrayList<String> found = new ArrayList<>();

        for (var classEntry : NyaView.loader.classes) {
            boolean classMatches = false;
            // Search Classes
            if (parameters.searchType == SearchType.CLASS || parameters.searchType == SearchType.CARPET_BOMB) {
                if (classEntry.oldMatch(parameters)) {
                    classMatches = true;
                    if (parameters.filterType == FilterType.SUPERSTRICT) {
                        found.add(classEntry.niceString(true));
                    }
                }
            }

            // Search Methods
            if (parameters.searchType == SearchType.METHOD || parameters.searchType == SearchType.CARPET_BOMB) {
                for (var methodEntry : classEntry.methods) {
                    if (methodEntry.oldMatch(parameters)) {
                        classMatches = true;
                        if (parameters.filterType == FilterType.SUPERSTRICT) {
                            found.add(methodEntry.niceString(true));
                        }
                    }

                }
            }

            // Search Fields
            if (parameters.searchType == SearchType.FIELD || parameters.searchType == SearchType.CARPET_BOMB) {
                for (var fieldEntry : classEntry.fields) {
                    if (fieldEntry.oldMatch(parameters)) {
                        classMatches = true;
                        if (parameters.filterType == FilterType.SUPERSTRICT) {
                            found.add(fieldEntry.niceString(true));
                        }
                    }
                }
            }

            if (classMatches && parameters.filterType != FilterType.SUPERSTRICT) {
                found.add(classEntry.niceString(false));
            }
        }

        return found;
    }

    public enum FilterType {
        FUZZY,
        STRICT,
        SUPERSTRICT
    }

    public enum SearchType {
        // Searches only class names
        CLASS,

        // Searches only method names
        METHOD,

        // Search only field names
        FIELD,

        // Carpet bombing go brrr, search everything, drop the bombs hell yeah girlll!!!
        CARPET_BOMB
    }

    public enum SearchMappings {
        OBFUSCATED,
        OBFUSCATED_CLIENT,
        OBFUSCATED_SERVER,
        MCP,
        MCP_CLIENT,
        MCP_SERVER,
        INTERMEDIARY,
        BABRIC,
        ALL
    }

    public static class SearchParameters {
        public SearchType searchType;
        public FilterType filterType;
        public SearchMappings mappings;
        public String query;

        @Override
        public String toString() {
            return "SearchParameters:" +
                    "\n  searchType = " + searchType +
                    "\n  filterType = " + filterType +
                    "\n  mappings = " + mappings +
                    "\n  query = " + query;
        }
    }

}
