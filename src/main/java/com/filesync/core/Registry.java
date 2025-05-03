package com.filesync.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registry {
    private final Map<String, Entry> entries = new HashMap<>();
    
    public void addEntry(String path, long lastModified) {
        entries.put(path, new Entry(path, lastModified));
    }
    
    public Entry getEntry(String path) {
        return entries.get(path);
    }
    
    public void removeEntry(String path) {
        entries.remove(path);
    }
    
    public List<Entry> getEntries() {
        return new ArrayList<>(entries.values());
    }
    
    public static class Entry {
        private final String path;
        private final long lastModified;
        
        public Entry(String path, long lastModified) {
            this.path = path;
            this.lastModified = lastModified;
        }
        
        public String getPath() {
            return path;
        }
        
        public long getLastModified() {
            return lastModified;
        }
    }
}
