package net.danygames2014.nyaview.search;

public class SearchParameters {
    public String query;
    public SearchType type;
    public MatchType match;
    public boolean caseSensitive;
    public SearchMappings mappings;

    public SearchParameters(String query, SearchType type, MatchType match, boolean caseSensitive, SearchMappings mappings) {
        this.query = query;
        this.type = type;
        this.match = match;
        this.caseSensitive = caseSensitive;
        this.mappings = mappings;
    }

    public enum MatchType {
        // It is enough that part of the query matches
        FUZZY,
        
        // The full query needs to match
        STRICT
    }
    
    public enum SearchType {
        // Carpet bombing go brrr, search everything, drop the bombs hell yeah girlll!!!
        CARPET_BOMB,
        
        // Searches only class names
        CLASS,

        // Searches only method names
        METHOD,

        // Search only field names
        FIELD
    }
    
    public enum SearchMappings {
        OBFUSCATED,
        OBFUSCATED_CLIENT,
        OBFUSCATED_SERVER,
        MCP,
        MCP_CLIENT,
        MCP_SERVER,
        INTERMEDIARY,
        FABRIC,
        ALL
    }
}
