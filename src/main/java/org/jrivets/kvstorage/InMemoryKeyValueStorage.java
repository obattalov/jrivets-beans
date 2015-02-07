package org.jrivets.kvstorage;

import java.util.HashMap;
import java.util.Map;

import org.jrivets.util.BinarySerializer;
import org.jrivets.util.CloseableLock;

public class InMemoryKeyValueStorage<K, V> implements KeyValueStorage<K, V> {
    
    private final CloseableLock lock = new CloseableLock();
    
    private Map<K, VersionedValue<V>> store = new HashMap<K, VersionedValue<V>>();

    @Override
    public int put(K key, V value) {
        try (CloseableLock l = lock.autounlock()) {
            return put(key, value, store.get(key));
        }
    }

    @Override
    public int cas(K key, V value, int expectedVersion) {
        try (CloseableLock l = lock.autounlock()) {
            VersionedValue<V> vv = store.get(key);
            if (vv == null || vv.version != expectedVersion) {
                return -1;
            }
            return put(key, value, vv);
        }
    }

    @Override
    public VersionedValue<V> get(K key) {
        try (CloseableLock l = lock.autounlock()) {
            return BinarySerializer.copy(store.get(key));
        }
    }

    @Override
    public VersionedValue<V> remove(K key) {
        try (CloseableLock l = lock.autounlock()) {
            return store.remove(key);
        }
    }

    @Override
    public VersionedValue<V> remove(K key, int expectedVersion) {
        try (CloseableLock l = lock.autounlock()) {
            VersionedValue<V> vv = store.get(key);
            if (vv == null || vv.version != expectedVersion) {
                return null;
            }
            return store.remove(key);
        }
    }

    private int newVersion(VersionedValue<V> vv) {
        if (vv == null || vv.version == Integer.MAX_VALUE) {
            return 1;
        }
        return vv.version + 1;
    }
    
    private int put(K key, V value, VersionedValue<V> prevVV) {
        V clone = BinarySerializer.copy(value);
        VersionedValue<V> vv = VersionedValue.get(clone, newVersion(prevVV));
        store.put(key, vv);
        return vv.version;
    }
    
}
