package net.danygames2014.nyaviewtest;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.search.OldSearch;

import java.util.ArrayList;
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
            OldSearch.SearchParameters parameters = parseSearchQuery(input);
            if (parameters == null) {
                System.err.println("Invalid Search Query");
                continue;
            }

            System.out.println(parameters);

            ArrayList<String> result = search(parameters);
            if (result.isEmpty()) {
                System.err.println("No Results");
            } else {
                for (var item : result) {
                    System.out.println(item);
                }
                System.out.println("Found " + result.size() + (result.size() > 1 ? " results" : " result"));
            }
        }
    }
}
