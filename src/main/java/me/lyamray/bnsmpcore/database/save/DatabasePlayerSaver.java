package me.lyamray.bnsmpcore.database.save;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;

import java.sql.SQLException;
import java.util.*;

public class DatabasePlayerSaver extends AbstractDatabaseSaver {
    @Getter
    private static final DatabasePlayerSaver instance = new DatabasePlayerSaver();

    @Override
    public String getTableName() {
        return "players";
    }

    @Override
    protected Iterable<Map<String, Object>> getAllEntriesToSave() throws SQLException {
        List<Map<String, Object>> entries = new ArrayList<>();
        for (PlayerData player : PlayerDataHandler.getInstance().getCacheMap().values()) {
            entries.add(Map.of(
                    "uuid", player.getUuid().toString(),
                    "name", player.getName(),
                    "money", player.getMoney(),
                    "playtime", player.getPlaytime(),
                    "rank", player.getRank()
            ));
        }
        return entries;
    }
}
