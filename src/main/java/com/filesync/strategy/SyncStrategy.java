package com.filesync.strategy;

import java.io.IOException;

// Strategy pattern
public interface SyncStrategy {
    void execute() throws IOException;
}
