package com.filesync.core.registry;

import com.filesync.core.Profile;
import com.filesync.core.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

// Note: This implementation uses org.json library
// You would need to add this dependency to your project
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Implementation of RegistryBuilder for JSON format.
 */
public class JSONRegistryBuilder implements RegistryBuilder {
    
    @Override
    public Registry buildRegistry(String filePath, Profile profile) throws IOException {
        Registry registry = new Registry();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return registry;
        }
        
        try (FileReader reader = new FileReader(file)) {
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject root = new JSONObject(tokener);
            
            JSONArray entries = root.getJSONArray("entries");
            
            for (int i = 0; i < entries.length(); i++) {
                JSONObject entry = entries.getJSONObject(i);
                String path = entry.getString("path");
                long lastModified = entry.getLong("lastModified");
                
                registry.addEntry(path, lastModified);
            }
        } catch (Exception e) {
            throw new IOException("Error parsing JSON registry: " + e.getMessage(), e);
        }
        
        return registry;
    }
    
    @Override
    public void saveRegistry(Registry registry, String filePath, Profile profile) throws IOException {
        JSONObject root = new JSONObject();
        root.put("profileName", profile.getName());
        
        JSONArray entries = new JSONArray();
        
        for (Registry.Entry entry : registry.getEntries()) {
            JSONObject entryObj = new JSONObject();
            entryObj.put("path", entry.getPath());
            entryObj.put("lastModified", entry.getLastModified());
            entries.put(entryObj);
        }
        
        root.put("entries", entries);
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(root.toString(2)); // Pretty print with 2-space indentation
        } catch (Exception e) {
            throw new IOException("Error saving JSON registry: " + e.getMessage(), e);
        }
    }
}
