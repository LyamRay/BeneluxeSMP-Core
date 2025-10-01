package me.lyamray.bnsmpcore.database.save;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.warps.WarpsData;
import me.lyamray.bnsmpcore.data.warps.WarpsDataHandler;
import me.lyamray.bnsmpcore.database.Database;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseWarpsSaver {

    @Getter
    private static final DatabaseWarpsSaver instance = new DatabaseWarpsSaver();
    private final Database database = Database.getInstance();

    public void saveAllWarps() {
        WarpsDataHandler.getInstance().getWarpsDataCache().values().forEach(this::saveWarp);
    }

    private void saveWarp(WarpsData warp) {
        try {
            boolean exists = database.exists("warps", "name = ?", warp.getName());
            Map<String, Object> values = Map.of(
                    "name", warp.getName(),
                    "x", warp.getX(),
                    "y", warp.getY(),
                    "z", warp.getZ(),
                    "world", warp.getWorld(),
                    "required_rank", warp.getRequiredRank()
            );

            if (exists) {
                database.set("warps", values, "name = ?", warp.getName());
            } else {
                database.add("warps", values);
            }
        } catch (SQLException e) {
            log.warn("Failed to save warp {}: {}", warp.getName(), e.getMessage());
        }
    }
}
