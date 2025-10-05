package me.lyamray.bnsmpcore.data.homes;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;
import me.lyamray.bnsmpcore.data.warps.WarpsData;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class HomesDataHandler extends AbstractDataHandler<Set<HomesData>> {

    @Getter
    private static final HomesDataHandler instance = new HomesDataHandler();

    public void addHome(Integer player, HomesData home) {
        cache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(home);
    }

    public void removeHome(UUID player, HomesData home) {
        WarpsData homes = cache.get(player);
        if (homes != null) homes.remove(home);
    }

    public boolean hasHome(UUID player, String homeName) {
        WarpsData homes = cache.get(player);
        return homes != null && homes.stream().anyMatch(h -> h.getHomeName().equals(homeName));
    }
}
