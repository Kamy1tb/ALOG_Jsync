package com.filesync.filesystem;

import java.io.IOException;

// Abstract Factory pattern
public interface FileSystemHandler {
    FileSystemNode scan() throws IOException;
    void copyFile(String sourcePath, String targetPath, boolean preserveTimestamp) throws IOException;
    void deleteFile(String path) throws IOException;
    boolean exists(String path);
    long getLastModified(String path);
    void setLastModified(String path, long time) throws IOException;
    String getBasePath();
}
