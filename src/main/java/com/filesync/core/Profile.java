package com.filesync.core;

public class Profile {
    private final String name;
    private final String pathA;
    private final String pathB;
    
    public Profile(String name, String pathA, String pathB) {
        this.name = name;
        this.pathA = pathA;
        this.pathB = pathB;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPathA() {
        return pathA;
    }
    
    public String getPathB() {
        return pathB;
    }
}
