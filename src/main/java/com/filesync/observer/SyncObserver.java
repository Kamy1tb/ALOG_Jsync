package com.filesync.observer;

// Observer pattern
public interface SyncObserver {
    void onSyncEvent(String event, String path);
}
