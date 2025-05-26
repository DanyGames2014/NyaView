package net.danygames2014.nyaview;

import net.danygames2014.nyaview.search.OldSearch;
import net.danygames2014.nyaview.search.SearchParameters;

public interface Searchable {
    @Deprecated
    boolean oldMatch(OldSearch.SearchParameters parameters);
    
    boolean match(SearchParameters parameters);
}
