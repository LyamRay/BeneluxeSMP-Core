package me.lyamray.bnsmpcore.database.save;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.database.Database;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabasePlayerSaver {

    @Getter
    private static final DatabasePlayerSaver instance = new DatabasePlayerSaver();
    private final Database database = Database.getInstance();

    public void saveAllPlayers() {
        PlayerDataHandler.getInstance().getPlayerDataCache().values().forEach(this::savePlayer);
    }

    private void savePlayer(PlayerData player) {
        try {
            boolean exists = database.exists("players", "uuid = ?", player.getUuid().toString());
            Map<String, Object> values = Map.of(
                    "uuid", player.getUuid().toString(),
                    "name", player.getName(),
                    "money", player.getMoney(),
                    "playtime", player.getPlaytime(),
                    "rank", player.getRank()
            );

            if (exists) {
                database.set("players", values, "uuid = ?", player.getUuid().toString());
            } else {
                database.add("players", values);
            }
        } catch (SQLException e) {
            log.warn("Failed to save player {}: {}", player.getUuid(), e.getMessage());
        }
    }
}
