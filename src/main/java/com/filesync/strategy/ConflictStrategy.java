package com.filesync.strategy;

import com.filesync.filesystem.FileSystemHandler;

import java.io.IOException;

// Strategy pattern implementation for conflict resolution
public class ConflictStrategy implements SyncStrategy {
    private final FileSystemHandler source;
    private final FileSystemHandler target;
    private final String path;
    private final boolean isSourceA;
    
    public ConflictStrategy(FileSystemHandler source, FileSystemHandler target, String path, boolean isSourceA) {
        this.source = source;
        this.target = target;
        this.path = path;
        this.isSourceA = isSourceA;
    }
    
    @Override
    public void execute() throws IOException {
        // Delegate to CopyStrategy with preserveTimestamp=true
        CopyStrategy copyStrategy = new CopyStrategy(source, target, path, true, isSourceA);
        copyStrategy.execute();
    }
    
    public boolean isSourceA() {
        return isSourceA;
    }
}
