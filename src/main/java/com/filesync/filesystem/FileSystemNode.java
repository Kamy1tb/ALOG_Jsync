package com.filesync.filesystem;

import java.util.ArrayList;
import java.util.List;

// Composite pattern
public class FileSystemNode {
    private final String name;
    private final boolean isDirectory;
    private final long lastModified;
    private final List<FileSystemNode> children;
    
    public FileSystemNode(String name, boolean isDirectory, long lastModified) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.lastModified = lastModified;
        this.children = isDirectory ? new ArrayList<>() : null;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isDirectory() {
        return isDirectory;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    public List<FileSystemNode> getChildren() {
        return children;
    }
    
    public void addChild(FileSystemNode child) {
        if (isDirectory && children != null) {
            children.add(child);
        }
    }
}
