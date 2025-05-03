package com.filesync.cli;

// Command pattern interface
public interface Command {
    void execute(String[] args) throws Exception;
}
