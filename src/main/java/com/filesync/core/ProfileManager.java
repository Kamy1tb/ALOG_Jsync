package com.filesync.core;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

// Singleton pattern
public class ProfileManager {
    private static ProfileManager instance;
    
    private ProfileManager() {}
    
    public static synchronized ProfileManager getInstance() {
        if (instance == null) {
            instance = new ProfileManager();
        }
        return instance;
    }
    
    public void saveProfile(Profile profile) throws IOException {
        Properties properties = new Properties();
        properties.setProperty("pathA", profile.getPathA());
        properties.setProperty("pathB", profile.getPathB());
        
        File profileFile = new File(profile.getName() + ".sync");
        try (FileWriter writer = new FileWriter(profileFile)) {
            properties.store(writer, "Sync Profile");
        }
    }
    
    public Profile loadProfile(String profileName) throws IOException {
        File profileFile = new File(profileName + ".sync");
        if (!profileFile.exists()) {
            return null;
        }
        
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(profileFile)) {
            properties.load(reader);
        }
        
        String pathA = properties.getProperty("pathA");
        String pathB = properties.getProperty("pathB");
        
        return new Profile(profileName, pathA, pathB);
    }
}
