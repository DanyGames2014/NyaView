package net.danygames2014.nyaviewtest;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.search.OldSearch;
import net.danygames2014.nyaview.search.Search;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchResult;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import static net.danygames2014.nyaview.search.OldSearch.parseSearchQuery;
import static net.danygames2014.nyaview.search.OldSearch.search;

public class NyaViewTest {
    public static boolean run = true; 
    
    public static void main(String[] args) {
        NyaView.init();

        Scanner sc = new Scanner(System.in);
        while (run) {
            String input = sc.nextLine();

            SearchParameters parameters = SearchParameters.parse(input);
            
            if (parameters == null) {
                System.err.println("Invalid search query");
                continue;
            }

            System.out.println(parameters);
            SearchResult result = Search.search(parameters);

            for (Map.Entry<ClassMappingEntry, SearchResult.SearchResultClassEntry> r : result.results.entrySet()) {
                //System.out.println(r.getKey());
            }

            System.out.println("Found " + result.methodCount + " methods and " + result.fieldCount + " fields in " + result.results.size() + (result.results.size() > 1 ? " classes" : " class"));
//            OldSearch.SearchParameters parameters = parseSearchQuery(input);
//            if (parameters == null) {
//                System.err.println("Invalid Search Query");
//                continue;
//            }
//
//            System.out.println(parameters);
//
//            ArrayList<String> result = search(parameters);
//            if (result.isEmpty()) {
//                System.err.println("No Results");
//            } else {
//                for (var item : result) {
//                    System.out.println(item);
//                }
//                System.out.println("Found " + result.size() + (result.size() > 1 ? " results" : " result"));
//            }
        }
    }
}
