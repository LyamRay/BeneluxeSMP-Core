package me.lyamray.bnsmpcore.database.save;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.homes.HomesData;
import me.lyamray.bnsmpcore.data.homes.HomesDataHandler;
import me.lyamray.bnsmpcore.database.Database;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseHomesSaver {

    @Getter
    private static final DatabaseHomesSaver instance = new DatabaseHomesSaver();
    private final Database database = Database.getInstance();

    public void saveAllHomes() {
        HomesDataHandler.getInstance().getHomeDataCache().forEach((player, homes) -> {
            for (HomesData home : homes) saveHome(player, home);
        });
    }

    private void saveHome(UUID player, HomesData home) {
        try {
            boolean exists = database.exists("homes", "player_uuid = ? AND home_name = ?", player.toString(), home.getHomeName());
            Map<String, Object> values = Map.of(
                    "player_uuid", player.toString(),
                    "home_name", home.getHomeName(),
                    "x", home.getX(),
                    "y", home.getY(),
                    "z", home.getZ(),
                    "world", home.getWorld()
            );

            if (exists) {
                database.set("homes", values, "player_uuid = ? AND home_name = ?", player.toString(), home.getHomeName());
            } else {
                database.add("homes", values);
            }
        } catch (SQLException e) {
            log.warn("Failed to save home {} for player {}: {}", home.getHomeName(), player, e.getMessage());
        }
    }
}
