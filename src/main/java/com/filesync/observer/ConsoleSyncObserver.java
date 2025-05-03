package com.filesync.observer;

// Observer pattern implementation
public class ConsoleSyncObserver implements SyncObserver {
    @Override
    public void onSyncEvent(String event, String path) {
        System.out.println("[" + event + "] " + path);
    }
}
