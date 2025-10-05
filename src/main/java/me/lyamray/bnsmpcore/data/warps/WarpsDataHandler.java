package me.lyamray.bnsmpcore.data.warps;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;

import java.util.Collection;
import java.util.Map;

@Getter
public class WarpsDataHandler extends AbstractDataHandler<WarpsData, String> {

    @Getter
    private static final WarpsDataHandler instance = new WarpsDataHandler();

    public Map<String, WarpsData> getCacheMap() {
        return cache;
    }

    public void setWarp(WarpsData warp) {
        cache.put(warp.getName(), warp);
    }

    public WarpsData getWarp(String name) {
        return cache.get(name);
    }

    public void removeWarp(String name) {
        cache.remove(name);
    }

    public boolean existsWarp(String name) {
        return cache.containsKey(name);
    }

    public Collection<WarpsData> getAllWarps() {
        return cache.values();
    }
}
