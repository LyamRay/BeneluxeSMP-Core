package me.lyamray.bnsmpcore.data.player;

import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerDataHandler {

    @Getter
    private static final PlayerDataHandler instance = new PlayerDataHandler();

    private final Map<UUID, PlayerData> playerDataCache = new ConcurrentHashMap<>();

    public void setData(PlayerData data) {
        playerDataCache.put(data.getUuid(), data);
    }

    public PlayerData getData(UUID uuid) {
        return playerDataCache.computeIfAbsent(uuid,
                id -> new PlayerData(id, "Unknown", 0, 0, "speler"));
    }

    public void addData(PlayerData data) {
        playerDataCache.putIfAbsent(data.getUuid(), data);
    }

    public void removeData(UUID uuid) {
        playerDataCache.remove(uuid);
    }

    public boolean hasData(UUID uuid) {
        return playerDataCache.containsKey(uuid);
    }
}