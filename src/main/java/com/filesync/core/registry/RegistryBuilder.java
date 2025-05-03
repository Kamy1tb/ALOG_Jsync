package com.filesync.core.registry;

import com.filesync.core.Profile;
import com.filesync.core.Registry;

import java.io.IOException;

/**
 * Builder interface for creating Registry objects from different formats.
 * Implements the Builder pattern.
 */
public interface RegistryBuilder {
    /**
     * Builds a Registry object from a file in the specified format.
     * 
     * @param filePath Path to the registry file
     * @param profile Profile associated with the registry
     * @return Registry object populated with entries from the file
     * @throws IOException If an I/O error occurs
     */
    Registry buildRegistry(String filePath, Profile profile) throws IOException;
    
    /**
     * Saves a Registry object to a file in the specified format.
     * 
     * @param registry Registry to save
     * @param filePath Path to the registry file
     * @param profile Profile associated with the registry
     * @throws IOException If an I/O error occurs
     */
    void saveRegistry(Registry registry, String filePath, Profile profile) throws IOException;
}
