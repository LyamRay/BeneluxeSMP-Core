package me.lyamray.beneluxesmpcore.database.save;

import lombok.Getter;
import me.lyamray.beneluxesmpcore.data.warps.WarpsData;
import me.lyamray.beneluxesmpcore.data.warps.WarpsDataHandler;

import java.sql.SQLException;
import java.util.*;

public class DatabaseWarpsSaver extends AbstractDatabaseSaver {
    @Getter
    private static final DatabaseWarpsSaver instance = new DatabaseWarpsSaver();

    @Override
    public String getTableName() {
        return "warps";
    }

    @Override
    protected Iterable<Map<String, Object>> getAllEntriesToSave() throws SQLException {
        List<Map<String, Object>> entries = new ArrayList<>();
        for (WarpsData warp : WarpsDataHandler.getInstance().getCacheMap().values()) {
            entries.add(Map.of(
                    "name", warp.getName(),
                    "x", warp.getX(),
                    "y", warp.getY(),
                    "z", warp.getZ(),
                    "world", warp.getWorld(),
                    "required_rank", warp.getRequiredRank()
            ));
        }
        return entries;
    }
}
