package me.lyamray.bnsmpcore.data;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.warps.WarpsData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public abstract class AbstractDataHandler<T> {
    protected final Map<Integer, WarpsData> cache = new ConcurrentHashMap<>();

    public WarpsData get(UUID player) {
        return cache.get(player);
    }

    public void set(Integer player, WarpsData data) {
        cache.put(player, data);
    }

    public boolean has(UUID player) {
        return cache.containsKey(player);
    }

}
