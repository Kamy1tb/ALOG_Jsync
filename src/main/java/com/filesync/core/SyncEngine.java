package com.filesync.core;

import com.filesync.filesystem.FileSystemFactory;
import com.filesync.filesystem.FileSystemHandler;
import com.filesync.filesystem.FileSystemNode;
import com.filesync.observer.SyncObserver;
import com.filesync.strategy.ConflictStrategy;
import com.filesync.strategy.CopyStrategy;
import com.filesync.strategy.DeleteStrategy;
import com.filesync.strategy.SyncStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

// Template Method pattern for the synchronization algorithm
public class SyncEngine {
    private final Profile profile;
    private final Registry registry;
    private final String format;
    private final List<SyncObserver> observers = new ArrayList<>();
    
    public SyncEngine(Profile profile) {
        this(profile, null, "properties");
    }
    
    public SyncEngine(Profile profile, Registry registry, String format) {
        this.profile = profile;
        this.registry = registry != null ? registry : new Registry();
        this.format = format;
    }
    
    public void registerObserver(SyncObserver observer) {
        observers.add(observer);
    }
    
    public void unregisterObserver(SyncObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyObservers(String event, String path) {
        for (SyncObserver observer : observers) {
            observer.onSyncEvent(event, path);
        }
    }
    
    public void synchronize() throws IOException {
        // Factory Method pattern to create file system handlers
        FileSystemHandler handlerA = FileSystemFactory.createFileSystemHandler(profile.getPathA());
        FileSystemHandler handlerB = FileSystemFactory.createFileSystemHandler(profile.getPathB());
        
        RegistryManager registryManager = RegistryManager.getInstance();
        
        // Scan both file systems
        FileSystemNode rootA = handlerA.scan();
        FileSystemNode rootB = handlerB.scan();
        
        // Build file maps for easier comparison
        Map<String, FileSystemNode> filesA = buildFileMap(rootA, "");
        Map<String, FileSystemNode> filesB = buildFileMap(rootB, "");
        
        // Process all paths from both sides
        Map<String, SyncStatus> statusMap = new HashMap<>();
        
        // Check files in A
        for (String path : filesA.keySet()) {
            FileSystemNode nodeA = filesA.get(path);
            FileSystemNode nodeB = filesB.get(path);
            Registry.Entry registryEntry = registry.getEntry(path);
            
            if (nodeA.isDirectory()) continue; // Skip directories, process only files
            
            SyncStatus status = determineSyncStatus(nodeA, nodeB, registryEntry);
            statusMap.put(path, status);
        }
        
        // Check files in B that are not in A
        for (String path : filesB.keySet()) {
            if (!filesA.containsKey(path)) {
                FileSystemNode nodeB = filesB.get(path);
                Registry.Entry registryEntry = registry.getEntry(path);
                
                if (nodeB.isDirectory()) continue; // Skip directories
                
                SyncStatus status = determineSyncStatus(null, nodeB, registryEntry);
                statusMap.put(path, status);
            }
        }
        
        // Process each file based on its status
        for (Map.Entry<String, SyncStatus> entry : statusMap.entrySet()) {
            String path = entry.getKey();
            SyncStatus status = entry.getValue();
            
            // Strategy pattern for different synchronization strategies
            SyncStrategy strategy;
            
            switch (status) {
                case A_NEW:
                    strategy = new CopyStrategy(handlerA, handlerB, path, true);
                    notifyObservers("COPY", "A -> B: " + path);
                    break;
                case B_NEW:
                    strategy = new CopyStrategy(handlerB, handlerA, path, true);
                    notifyObservers("COPY", "B -> A: " + path);
                    break;
                case A_NEWER:
                    strategy = new CopyStrategy(handlerA, handlerB, path, false);
                    notifyObservers("UPDATE", "A -> B: " + path);
                    break;
                case B_NEWER:
                    strategy = new CopyStrategy(handlerB, handlerA, path, false);
                    notifyObservers("UPDATE", "B -> A: " + path);
                    break;
                case CONFLICT:
                    strategy = resolveConflict(path, handlerA, handlerB);
                    break;
                case A_DELETED:
                    strategy = new DeleteStrategy(handlerB, path);
                    notifyObservers("DELETE", "B: " + path);
                    break;
                case B_DELETED:
                    strategy = new DeleteStrategy(handlerA, path);
                    notifyObservers("DELETE", "A: " + path);
                    break;
                case NO_CHANGE:
                default:
                    continue; // No action needed
            }
            
            // Execute the strategy
            strategy.execute();
            
            // Update registry
            if (status == SyncStatus.A_DELETED || status == SyncStatus.B_DELETED) {
                registry.removeEntry(path);
            } else {
                long lastModified;
                if (status == SyncStatus.A_NEW || status == SyncStatus.A_NEWER || 
                    (status == SyncStatus.CONFLICT && strategy instanceof CopyStrategy && 
                     ((CopyStrategy) strategy).isSourceA())) {
                    lastModified = filesA.get(path).getLastModified();
                } else {
                    lastModified = filesB.get(path).getLastModified();
                }
                registry.addEntry(path, lastModified);
            }
        }
        
        // Save the updated registry with the specified format
        registryManager.saveRegistry(registry, profile, format);
    }
    
    // Rest of the methods remain the same
    
    private Map<String, FileSystemNode> buildFileMap(FileSystemNode node, String path) {
        Map<String, FileSystemNode> map = new HashMap<>();
        
        if (!node.isDirectory()) {
            map.put(path, node);
            return map;
        }
        
        for (FileSystemNode child : node.getChildren()) {
            String childPath = path.isEmpty() ? child.getName() : path + "/" + child.getName();
            map.put(childPath, child);
            
            if (child.isDirectory()) {
                map.putAll(buildFileMap(child, childPath));
            }
        }
        
        return map;
    }
    
    private SyncStatus determineSyncStatus(FileSystemNode nodeA, FileSystemNode nodeB, Registry.Entry registryEntry) {
        boolean hasA = nodeA != null;
        boolean hasB = nodeB != null;
        boolean hasRegistry = registryEntry != null;
        
        if (hasA && !hasB) {
            if (!hasRegistry) {
                return SyncStatus.A_NEW;
            } else if (nodeA.getLastModified() == registryEntry.getLastModified()) {
                return SyncStatus.B_DELETED;
            } else {
                return SyncStatus.CONFLICT;
            }
        } else if (!hasA && hasB) {
            if (!hasRegistry) {
                return SyncStatus.B_NEW;
            } else if (nodeB.getLastModified() == registryEntry.getLastModified()) {
                return SyncStatus.A_DELETED;
            } else {
                return SyncStatus.CONFLICT;
            }
        } else if (hasA && hasB) {
            if (!hasRegistry) {
                if (nodeA.getLastModified() > nodeB.getLastModified()) {
                    return SyncStatus.A_NEWER;
                } else if (nodeB.getLastModified() > nodeA.getLastModified()) {
                    return SyncStatus.B_NEWER;
                } else {
                    return SyncStatus.NO_CHANGE;
                }
            } else {
                boolean aChanged = nodeA.getLastModified() != registryEntry.getLastModified();
                boolean bChanged = nodeB.getLastModified() != registryEntry.getLastModified();
                
                if (aChanged && bChanged) {
                    return SyncStatus.CONFLICT;
                } else if (aChanged) {
                    return SyncStatus.A_NEWER;
                } else if (bChanged) {
                    return SyncStatus.B_NEWER;
                } else {
                    return SyncStatus.NO_CHANGE;
                }
            }
        }
        
        return SyncStatus.NO_CHANGE;
    }
    
    private SyncStrategy resolveConflict(String path, FileSystemHandler handlerA, FileSystemHandler handlerB) {
        System.out.println("CONFLICT detected for: " + path);
        System.out.println("Choose direction:");
        System.out.println("1. A -> B (use version from A)");
        System.out.println("2. B -> A (use version from B)");
        
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        do {
            System.out.print("Enter choice (1 or 2): ");
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                choice = 0;
                scanner.nextLine(); // Clear the invalid input
            }
        } while (choice != 1 && choice != 2);
        
        if (choice == 1) {
            notifyObservers("RESOLVE CONFLICT", "A -> B: " + path);
            return new ConflictStrategy(handlerA, handlerB, path, true);
        } else {
            notifyObservers("RESOLVE CONFLICT", "B -> A: " + path);
            return new ConflictStrategy(handlerB, handlerA, path, false);
        }
    }
    
    public enum SyncStatus {
        NO_CHANGE,
        A_NEW,
        B_NEW,
        A_NEWER,
        B_NEWER,
        A_DELETED,
        B_DELETED,
        CONFLICT
    }
}
