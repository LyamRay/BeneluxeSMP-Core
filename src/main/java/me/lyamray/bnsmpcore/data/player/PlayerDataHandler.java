package me.lyamray.bnsmpcore.data.player;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;

import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerDataHandler extends AbstractDataHandler<PlayerData, UUID> {

    @Getter
    private static final PlayerDataHandler instance = new PlayerDataHandler();

    public Map<UUID, PlayerData> getCacheMap() {
        return cache;
    }

    public void addData(PlayerData data) {
        cache.putIfAbsent(data.getUuid(), data);
    }

    public void setData(PlayerData data) {
        cache.put(data.getUuid(), data);
    }

    public PlayerData getData(UUID uuid) {
        return cache.computeIfAbsent(uuid,
                id -> new PlayerData(id, "Unknown", 0, 0, "Overlever", true, 0));
    }
}
