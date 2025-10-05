package me.lyamray.bnsmpcore.data.homes;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class HomesDataHandler extends AbstractDataHandler<Set<HomesData>, UUID> {

    @Getter
    private static final HomesDataHandler instance = new HomesDataHandler();

    public Map<UUID, Set<HomesData>> getCacheMap() {
        return cache;
    }

    public void addHome(UUID player, HomesData home) {
        cache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(home);
    }

    public void removeHome(UUID player, HomesData home) {
        Set<HomesData> homes = cache.get(player);
        if (homes != null) homes.remove(home);
    }

    public boolean hasHome(UUID player, String homeName) {
        Set<HomesData> homes = cache.get(player);
        return homes != null && homes.stream().anyMatch(h -> h.getHomeName().equals(homeName));
    }
}
