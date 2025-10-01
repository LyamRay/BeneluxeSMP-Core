package me.lyamray.bnsmpcore.data.homes;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class HomesDataHandler {

    @Getter
    private static final HomesDataHandler instance = new HomesDataHandler();

    private final Map<UUID, Set<HomesData>> homeDataCache = new ConcurrentHashMap<>();

    public void setHomes(UUID player, Set<HomesData> homes) {
        homeDataCache.put(player, ConcurrentHashMap.newKeySet(homes.size()));
        homeDataCache.get(player).addAll(homes);
    }

    public Set<HomesData> getHomes(UUID player) {
        return homeDataCache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
    }

    public void addHome(UUID player, HomesData home) {
        homeDataCache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(home);
    }

    public void removeHome(UUID player, HomesDataHandler home) {
        Set<HomesData> homes = homeDataCache.get(player);
        if (homes != null) homes.remove(home);
    }

    public boolean hasHome(UUID player, String homeName) {
        Set<HomesData> homes = homeDataCache.get(player);
        return homes != null && homes.stream().anyMatch(h -> h.getHomeName().equals(homeName));
    }
}
