package com.filesync.cli;

import com.filesync.core.Profile;
import com.filesync.core.ProfileManager;
import com.filesync.core.Registry;
import com.filesync.core.RegistryManager;
import com.filesync.core.SyncEngine;
import com.filesync.observer.ConsoleSyncObserver;

public class SyncCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: sync <profile-name> [format]");
        }
        
        String profileName = args[0];
        String format = args.length > 1 ? args[1] : "xml"; // Changed default to xml
        
        ProfileManager profileManager = ProfileManager.getInstance();
        Profile profile = profileManager.loadProfile(profileName);
        
        if (profile == null) {
            throw new IllegalArgumentException("Profile not found: " + profileName);
        }
        
        RegistryManager registryManager = RegistryManager.getInstance();
        Registry registry = registryManager.loadRegistry(profile, format);
        
        SyncEngine syncEngine = new SyncEngine(profile, registry, format); // Pass registry and format to SyncEngine
        
        // Register observer for console output (Observer pattern)
        syncEngine.registerObserver(new ConsoleSyncObserver());
        
        syncEngine.synchronize();
        
        System.out.println("Synchronization completed for profile '" + profileName + "'.");
    }
}
