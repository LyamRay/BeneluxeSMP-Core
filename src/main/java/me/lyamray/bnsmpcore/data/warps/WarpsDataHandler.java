package me.lyamray.bnsmpcore.data.warps;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;

import java.util.Collection;

@Getter
public class WarpsDataHandler extends AbstractDataHandler<WarpsData> {

    @Getter
    private static final WarpsDataHandler instance = new WarpsDataHandler();

    public void setWarp(WarpsData warp) {
        cache.put(warp.getName().hashCode(), warp);
    }

    public WarpsData getWarp(String name) {
        return cache.get(name.hashCode());
    }

    public void removeWarp(String name) {
        cache.remove(name.hashCode());
    }

    public boolean existsWarp(String name) {
        return cache.containsKey(name.hashCode());
    }

    public Collection<WarpsData> getAllWarps() {
        return cache.values();
    }
}
