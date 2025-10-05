package me.lyamray.bnsmpcore.database.save;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.homes.HomesData;
import me.lyamray.bnsmpcore.data.homes.HomesDataHandler;

import java.sql.SQLException;
import java.util.*;

public class DatabaseHomesSaver extends AbstractDatabaseSaver {
    @Getter
    private static final DatabaseHomesSaver instance = new DatabaseHomesSaver();

    @Override
    public String getTableName() {
        return "homes";
    }

    @Override
    protected Iterable<Map<String, Object>> getAllEntriesToSave() throws SQLException {
        List<Map<String, Object>> entries = new ArrayList<>();
        HomesDataHandler.getInstance().getCacheMap().forEach((player, homes) -> {
            for (HomesData home : homes) {
                entries.add(Map.of(
                        "player_uuid", player.toString(),
                        "home_name", home.getHomeName(),
                        "x", home.getX(),
                        "y", home.getY(),
                        "z", home.getZ(),
                        "world", home.getWorld()
                ));
            }
        });
        return entries;
    }
}
