package com.filesync.cli;

import java.util.HashMap;
import java.util.Map;

// Command pattern for CLI commands
public class CommandLineParser {
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void parse(String[] args) throws Exception {
        String commandName = args[0];
        Command command = commands.get(commandName);
        
        if (command == null) {
            throw new IllegalArgumentException("Unknown command: " + commandName);
        }
        
        String[] commandArgs = new String[args.length - 1];
        System.arraycopy(args, 1, commandArgs, 0, args.length - 1);
        
        command.execute(commandArgs);
    }
}
