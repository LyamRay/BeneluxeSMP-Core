package me.lyamray.bnsmpcore.data.player;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;
import me.lyamray.bnsmpcore.data.warps.WarpsData;

import java.util.UUID;

@Getter
public class PlayerDataHandler extends AbstractDataHandler<PlayerData> {

    @Getter
    private static final PlayerDataHandler instance = new PlayerDataHandler();

    public void setData(WarpsData data) {
        cache.put(data.getUuid(), data);
    }

    public WarpsData getData(Integer uuid) {
        return cache.computeIfAbsent(uuid,
                id -> new PlayerData(id, "Unknown", 0, 0, "speler"));
    }

    public void removeData(UUID uuid) {
        cache.remove(uuid);
    }
}
