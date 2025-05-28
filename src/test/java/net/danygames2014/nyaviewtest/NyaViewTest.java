package net.danygames2014.nyaviewtest;

import net.danygames2014.nyaview.NyaView;
import net.danygames2014.nyaview.search.DisplayParameters;
import net.danygames2014.nyaview.search.Search;
import net.danygames2014.nyaview.search.SearchParameters;
import net.danygames2014.nyaview.search.SearchResult;

import java.util.Scanner;

public class NyaViewTest {
    public static boolean run = true;

    public static void main(String[] args) {
        NyaView.init();

        Scanner sc = new Scanner(System.in);
        while (run) {
            String input = sc.nextLine();

            SearchParameters parameters = SearchParameters.parse(input);
            DisplayParameters displayParameters = new DisplayParameters();

            if (parameters == null) {
                System.err.println("Invalid search query");
                continue;
            }

            displayParameters.classDisplay = parameters.classDisplay;

            SearchResult result = Search.search(parameters);
            Search.printResult(result, parameters, displayParameters);
        }
    }
}
