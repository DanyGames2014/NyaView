package net.danygames2014.nyaview.search;

public class DisplayParameters {
    public ClassDisplay classDisplay;
    public Verbosity classVerbosity;
    public Verbosity methodVerbosity;
    public Verbosity fieldVerbosity;
    
    public enum ClassDisplay {
        // Show info about the class, its methods and its fields
        FULL,
        
        // Show info about the class and its methods
        ONLY_METHODS,
        
        // Show info about the class and its fields
        ONLY_FIELDS,
        
        // Only show the info about the class itself
        MINIMAL,
    
        // Will not show any info about the class at all
        // Note: This is invalid when searching for classes
        NONE
    }
    
    public enum Verbosity {
        FULL,
        REDUCED,
        MINIMAL
    }
}
