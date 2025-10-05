package me.lyamray.bnsmpcore.database.load;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.homes.HomesData;
import me.lyamray.bnsmpcore.data.homes.HomesDataHandler;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class DatabaseHomesLoader extends AbstractDatabaseLoader {
    @Getter
    private static final DatabaseHomesLoader instance = new DatabaseHomesLoader();

    @Override
    public String getTableName() {
        return "homes";
    }

    @Override
    protected void handleRow(Map<String, Object> row) {
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
}
