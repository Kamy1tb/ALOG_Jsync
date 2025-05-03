package com.filesync;

import com.filesync.cli.CommandLineParser;
import com.filesync.cli.NewProfileCommand;
import com.filesync.cli.SyncCommand;
import com.filesync.cli.SyncStatCommand;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            return;
        }

        CommandLineParser parser = new CommandLineParser();
        parser.registerCommand("new-profile", new NewProfileCommand());
        parser.registerCommand("sync", new SyncCommand());
        parser.registerCommand("syncstat", new SyncStatCommand());
        
        try {
            parser.parse(args);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  new-profile <profile-name> <path-to-folder-A> <path-to-folder-B>");
        System.out.println("  sync <profile-name>");
        System.out.println("  syncstat <profile-name>");
    }
}
