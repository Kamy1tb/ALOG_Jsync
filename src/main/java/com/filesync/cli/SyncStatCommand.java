package com.filesync.cli;

import com.filesync.core.Profile;
import com.filesync.core.ProfileManager;
import com.filesync.core.Registry;
import com.filesync.core.RegistryManager;

public class SyncStatCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: syncstat <profile-name> [format]");
        }
        
        String profileName = args[0];
        String format = args.length > 1 ? args[1] : "xml"; // Default to properties
        
        ProfileManager profileManager = ProfileManager.getInstance();
        Profile profile = profileManager.loadProfile(profileName);
        
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found: " + profileName);
        }
        
        System.out.println("Profile: " + profile.getName());
        System.out.println("Folder A: " + profile.getPathA());
        System.out.println("Folder B: " + profile.getPathB());
        System.out.println("Format: " + format);
        
        RegistryManager registryManager = RegistryManager.getInstance();
        Registry registry = registryManager.loadRegistry(profile, format);
        
        System.out.println("\nRegistry entries:");
        if (registry.getEntries().isEmpty()) {
            System.out.println("  No entries found (no synchronization performed yet)");
        } else {
            for (Registry.Entry entry : registry.getEntries()) {
                System.out.println("  " + entry.getPath() + " (Last modified: " + entry.getLastModified() + ")");
            }
        }
    }
}
