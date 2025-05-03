package com.filesync.core.registry;

import com.filesync.core.Profile;
import com.filesync.core.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of RegistryBuilder for Properties format.
 * This maintains backward compatibility with the original implementation.
 */
public class PropertiesRegistryBuilder implements RegistryBuilder {
    
    @Override
    public Registry buildRegistry(String filePath, Profile profile) throws IOException {
        Registry registry = new Registry();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return registry;
        }
        
        Properties properties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            properties.load(reader);
        }
        
        for (String path : properties.stringPropertyNames()) {
            long lastModified = Long.parseLong(properties.getProperty(path));
            registry.addEntry(path, lastModified);
        }
        
        return registry;
    }
    
    @Override
    public void saveRegistry(Registry registry, String filePath, Profile profile) throws IOException {
        Properties properties = new Properties();
        
        for (Registry.Entry entry : registry.getEntries()) {
            properties.setProperty(entry.getPath(), String.valueOf(entry.getLastModified()));
        }
        
        try (FileWriter writer = new FileWriter(filePath)) {
            properties.store(writer, "Sync Registry for profile: " + profile.getName());
        }
    }
}
