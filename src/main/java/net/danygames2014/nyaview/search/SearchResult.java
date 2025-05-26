package net.danygames2014.nyaview.search;

import net.danygames2014.nyaview.mapping.entry.ClassMappingEntry;
import net.danygames2014.nyaview.mapping.entry.FieldMappingEntry;
import net.danygames2014.nyaview.mapping.entry.MethodMappingEntry;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchResult {
    public HashMap<ClassMappingEntry, SearchResultClassEntry> results;

    public SearchResult() {
        results = new HashMap<>();
    }

    public void add(ClassMappingEntry classEntry) {
        if (classEntry == null) {
            return;
        }

        if (!results.containsKey(classEntry)) {
            results.put(classEntry, new SearchResultClassEntry());
        }
    }

    public void add(MethodMappingEntry methodEntry) {
        if (methodEntry == null) {
            return;
        }
        
        if (!results.containsKey(methodEntry.classEntry)) {
            results.put(methodEntry.classEntry, new SearchResultClassEntry());
        }
        
        if (!results.get(methodEntry.classEntry).methods.contains(methodEntry)) {
            results.get(methodEntry.classEntry).methods.add(methodEntry);
        }
    }

    public void add(FieldMappingEntry fieldEntry) {
        if (fieldEntry == null) {
            return;
        }

        if (!results.containsKey(fieldEntry.classEntry)) {
            results.put(fieldEntry.classEntry, new SearchResultClassEntry());
        }

        if (!results.get(fieldEntry.classEntry).fields.contains(fieldEntry)) {
            results.get(fieldEntry.classEntry).fields.add(fieldEntry);
        }
    }

    public static final class SearchResultClassEntry {
        public final ArrayList<MethodMappingEntry> methods;
        public final ArrayList<FieldMappingEntry> fields;

        public SearchResultClassEntry() {
            this.methods = new ArrayList<>();
            this.fields = new ArrayList<>();
        }
    }
}
