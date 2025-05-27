package net.danygames2014.nyaviewtest;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.search.Search;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchResult;

import java.util.Map;
import java.util.Scanner;

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
        }
    }
}
