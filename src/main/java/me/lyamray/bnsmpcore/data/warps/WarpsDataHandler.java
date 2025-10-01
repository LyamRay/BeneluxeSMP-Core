package me.lyamray.bnsmpcore.data.warps;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class WarpsDataHandler {

    @Getter
    private static final WarpsDataHandler instance = new WarpsDataHandler();

    private final Map<String, WarpsData> warpsDataCache = new ConcurrentHashMap<>();

    public void setWarp(WarpsData warp) {
        warpsDataCache.put(warp.getName(), warp);
    }

    public WarpsData getWarp(String name) {
        return warpsDataCache.get(name);
    }

    public void removeWarp(String name) {
        warpsDataCache.remove(name);
    }

    public boolean existsWarp(String name) {
        return warpsDataCache.containsKey(name);
    }

    public Collection<WarpsData> getAllWarps() {
        return warpsDataCache.values();
    }
}
