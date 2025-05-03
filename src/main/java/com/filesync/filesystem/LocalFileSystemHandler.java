package com.filesync.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class LocalFileSystemHandler implements FileSystemHandler {
    private final String basePath;
    
    public LocalFileSystemHandler(String basePath) {
        this.basePath = basePath;
    }
    
    @Override
    public FileSystemNode scan() throws IOException {
        File root = new File(basePath);
        return scanRecursive(root);
    }
    
    private FileSystemNode scanRecursive(File file) throws IOException {
        String name = file.getName();
        boolean isDirectory = file.isDirectory();
        long lastModified = file.lastModified();
        
        FileSystemNode node = new FileSystemNode(name, isDirectory, lastModified);
        
        if (isDirectory) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!isSymbolicLink(child)) { // Skip symbolic links
                        node.addChild(scanRecursive(child));
                    }
                }
            }
        }
        
        return node;
    }
    
    private boolean isSymbolicLink(File file) throws IOException {
        return Files.isSymbolicLink(file.toPath());
    }
    
    @Override
    public void copyFile(String sourcePath, String targetPath, boolean preserveTimestamp) throws IOException {
        Path source = Paths.get(basePath, sourcePath);
        Path target = Paths.get(targetPath);
        
        // Create parent directories if they don't exist
        File targetParent = target.getParent().toFile();
        if (!targetParent.exists()) {
            targetParent.mkdirs();
        }
        
        if (preserveTimestamp) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } else {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    @Override
    public void deleteFile(String path) throws IOException {
        Path filePath = Paths.get(basePath, path);
        Files.delete(filePath);
    }
    
    @Override
    public boolean exists(String path) {
        Path filePath = Paths.get(basePath, path);
        return Files.exists(filePath);
    }
    
    @Override
    public long getLastModified(String path) {
        Path filePath = Paths.get(basePath, path);
        return filePath.toFile().lastModified();
    }
    
    @Override
    public void setLastModified(String path, long time) throws IOException {
        Path filePath = Paths.get(basePath, path);
        filePath.toFile().setLastModified(time);
    }
    
    @Override
    public String getBasePath() {
        return basePath;
    }
}
