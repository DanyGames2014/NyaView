package net.danygames2014.nyaview.util;

public interface Searchable {
    boolean match(Search.SearchParameters parameters);

    String niceString(boolean superstrict);
}
