package me.lyamray.bnsmpcore.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataHandler<T, K> {
    protected final Map<K, T> cache = new ConcurrentHashMap<>();

    public T get(K key) {
        return cache.get(key);
    }

    public void set(K key, T value) {
        cache.put(key, value);
    }

    public boolean has(K key) {
        return cache.containsKey(key);
    }

    public void remove(K key) {
        cache.remove(key);
    }
}
