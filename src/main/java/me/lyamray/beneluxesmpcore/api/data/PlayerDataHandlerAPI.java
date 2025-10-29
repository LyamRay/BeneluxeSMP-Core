package me.lyamray.beneluxesmpcore.api.data;

import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerDataHandlerAPI {

    private static final PlayerDataHandler handler = PlayerDataHandler.getInstance();

    /**
     * Get all cached player data.
     * Returns an unmodifiable map to prevent external modifications.
     */
    public static Map<UUID, PlayerData> getAllPlayerData() {
        return Collections.unmodifiableMap(handler.getCacheMap());
    }

    /**
     * Get the PlayerData for a player.
     * If not found, a default PlayerData object will be created.
     */
    public static PlayerData getData(UUID uuid) {
        return handler.getData(uuid);
    }

    /**
     * Get the PlayerData for a player if already cached (without creating a new one).
     */
    public static Optional<PlayerData> getDataIfPresent(UUID uuid) {
        return Optional.ofNullable(handler.getCacheMap().get(uuid));
    }

    /**
     * Add a player data entry if absent.
     */
    public static void addData(PlayerData data) {
        handler.addData(data);
    }

    /**
     * Force-set (override) a player's data.
     */
    public static void setData(PlayerData data) {
        handler.setData(data);
    }

    /**
     * Remove a player's data from cache.
     */
    public static void removeData(UUID uuid) {
        handler.getCacheMap().remove(uuid);
    }

    /**
     * Check if a player's data is cached.
     */
    public static boolean isCached(UUID uuid) {
        return handler.getCacheMap().containsKey(uuid);
    }
}