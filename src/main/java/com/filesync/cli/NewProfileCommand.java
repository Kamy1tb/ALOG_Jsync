package com.filesync.cli;

import com.filesync.core.Profile;
import com.filesync.core.ProfileManager;

public class NewProfileCommand implements Command {
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 3) {
            throw new IllegalArgumentException("Usage: new-profile <profile-name> <path-to-folder-A> <path-to-folder-B>");
        }
        
        String profileName = args[0];
        String pathA = args[1];
        String pathB = args[2];
        
        ProfileManager profileManager = ProfileManager.getInstance();
        Profile profile = new Profile(profileName, pathA, pathB);
        profileManager.saveProfile(profile);
        
        System.out.println("Profile '" + profileName + "' created successfully.");
    }
}
