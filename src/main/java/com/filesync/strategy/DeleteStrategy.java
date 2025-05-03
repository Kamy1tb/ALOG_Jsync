package com.filesync.strategy;

import com.filesync.filesystem.FileSystemHandler;

import java.io.IOException;

// Strategy pattern implementation
public class DeleteStrategy implements SyncStrategy {
    private final FileSystemHandler handler;
    private final String path;
    
    public DeleteStrategy(FileSystemHandler handler, String path) {
        this.handler = handler;
        this.path = path;
    }
    
    @Override
    public void execute() throws IOException {
        handler.deleteFile(path);
    }
}
