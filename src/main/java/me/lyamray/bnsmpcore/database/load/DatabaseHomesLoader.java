package me.lyamray.bnsmpcore.database.load;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.homes.HomesData;
import me.lyamray.bnsmpcore.data.homes.HomesDataHandler;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.tasks.Task;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseHomesLoader {

    @Getter
    private static final DatabaseHomesLoader instance = new DatabaseHomesLoader();
    private final Database database = Database.getInstance();

    public void loadHomesAsync(Runnable afterLoad) {
        Task.runAsync(BeneluxeSMPCore.getInstance(), () -> {
            loadHomes();
            if (afterLoad != null) Task.runSync(BeneluxeSMPCore.getInstance(), afterLoad);
        });
    }

    private void loadHomes() {
        try {
            List<Map<String, Object>> results = database.get("homes", null);
            for (Map<String, Object> row : results) {
                UUID player = UUID.fromString((String) row.get("player_uuid"));
                HomesData home = new HomesData(
                        player,
                        (String) row.get("home_name"),
                        ((Number) row.get("x")).intValue(),
                        ((Number) row.get("y")).intValue(),
                        ((Number) row.get("z")).intValue(),
                        (String) row.get("world")
                );
                HomesDataHandler.getInstance().addHome(player, home);
            }
        } catch (SQLException e) {
            log.warn("Failed to load homes: {}", e.getMessage());
            BeneluxeSMPCore.getInstance().onDisable();
        }
    }
}
