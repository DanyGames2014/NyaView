package net.danygames2014.nyaview;

import net.danygames2014.nyaview.search.DisplayParameters;
import net.danygames2014.nyaview.search.SearchParameters;

public interface Searchable {
    boolean match(SearchParameters parameters);
    
    String searchString(DisplayParameters parameters);
}
