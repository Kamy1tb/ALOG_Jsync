package com.filesync.filesystem;

// Factory Method pattern
public class FileSystemFactory {
    public static FileSystemHandler createFileSystemHandler(String path) {
        // Currently only supports local file system
        // In the future, could return different implementations based on the path
        // (e.g., WebDAVFileSystemHandler for http:// paths)
        return new LocalFileSystemHandler(path);
    }
}
