package me.lyamray.bnsmpcore.database.load;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.warps.WarpsData;
import me.lyamray.bnsmpcore.data.warps.WarpsDataHandler;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.tasks.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseWarpsLoader {

    @Getter
    private static final DatabaseWarpsLoader instance = new DatabaseWarpsLoader();
    private final Database database = Database.getInstance();

    public void loadWarpsAsync(Runnable afterLoad) {
        Task.runAsync(BeneluxeSMPCore.getInstance(), () -> {
            loadWarps();
            if (afterLoad != null) Task.runSync(BeneluxeSMPCore.getInstance(), afterLoad);
        });
    }

    private void loadWarps() {
        try {
            List<Map<String, Object>> results = database.get("warps", null);
            for (Map<String, Object> row : results) {
                WarpsData warp = new WarpsData(
                        (String) row.get("name"),
                        ((Number) row.get("x")).intValue(),
                        ((Number) row.get("y")).intValue(),
                        ((Number) row.get("z")).intValue(),
                        (String) row.get("world"),
                        (String) row.get("required_rank")
                );
                WarpsDataHandler.getInstance().setWarp(warp);
            }
        } catch (SQLException e) {
            log.warn("Failed to load warps: {}", e.getMessage());
            BeneluxeSMPCore.getInstance().onDisable();
        }
    }
}
