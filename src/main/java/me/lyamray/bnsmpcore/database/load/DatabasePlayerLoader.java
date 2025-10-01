package me.lyamray.bnsmpcore.database.load;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.tasks.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabasePlayerLoader {

    @Getter
    private static final DatabasePlayerLoader instance = new DatabasePlayerLoader();
    private final Database database = Database.getInstance();

    public void loadPlayersAsync(Runnable afterLoad) {
        Task.runAsync(BeneluxeSMPCore.getInstance(), () -> {
            loadPlayers();
            if (afterLoad != null) Task.runSync(BeneluxeSMPCore.getInstance(), afterLoad);
        });
    }

    private void loadPlayers() {
        try {
            List<Map<String, Object>> results = database.get("players", null);
            for (Map<String, Object> row : results) {
                UUID uuid = UUID.fromString((String) row.get("uuid"));
                String name = (String) row.get("name");
                int money = ((Number) row.get("money")).intValue();
                int playtime = ((Number) row.get("playtime")).intValue();
                String rank = (String) row.get("rank");

                PlayerData data = new PlayerData(uuid, name, money, playtime, rank);
                PlayerDataHandler.getInstance().setData(data);
            }
        } catch (SQLException e) {
            log.warn("Failed to load players: {}", e.getMessage());
            BeneluxeSMPCore.getInstance().onDisable();
        }
    }
}
