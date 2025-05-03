package com.filesync.strategy;

import com.filesync.filesystem.FileSystemHandler;

import java.io.IOException;

// Strategy pattern implementation
public class CopyStrategy implements SyncStrategy {
    private final FileSystemHandler source;
    private final FileSystemHandler target;
    private final String path;
    private final boolean preserveTimestamp;
    private final boolean isSourceA;
    
    public CopyStrategy(FileSystemHandler source, FileSystemHandler target, String path, boolean preserveTimestamp) {
        this.source = source;
        this.target = target;
        this.path = path;
        this.preserveTimestamp = preserveTimestamp;
        this.isSourceA = true; // Default assumption
    }
    
    public CopyStrategy(FileSystemHandler source, FileSystemHandler target, String path, boolean preserveTimestamp, boolean isSourceA) {
        this.source = source;
        this.target = target;
        this.path = path;
        this.preserveTimestamp = preserveTimestamp;
        this.isSourceA = isSourceA;
    }
    
    @Override
    public void execute() throws IOException {
        String sourcePath = path;
        String targetPath = target.getBasePath() + "/" + path;
        
        source.copyFile(sourcePath, targetPath, preserveTimestamp);
        
        if (preserveTimestamp) {
            long lastModified = source.getLastModified(sourcePath);
            target.setLastModified(path, lastModified);
        }
    }
    
    public boolean isSourceA() {
        return isSourceA;
    }
}
