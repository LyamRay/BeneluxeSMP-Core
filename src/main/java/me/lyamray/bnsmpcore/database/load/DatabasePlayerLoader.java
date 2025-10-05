package me.lyamray.bnsmpcore.database.load;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;

import java.util.Map;
import java.util.UUID;

@Slf4j
public class DatabasePlayerLoader extends AbstractDatabaseLoader {
    @Getter
    private static final DatabasePlayerLoader instance = new DatabasePlayerLoader();

    @Override
    public String getTableName() {
        return "players";
    }

    @Override
    protected void handleRow(Map<String, Object> row) {
        UUID uuid = UUID.fromString((String) row.get("uuid"));
        String name = (String) row.get("name");
        int money = ((Number) row.get("money")).intValue();
        int playtime = ((Number) row.get("playtime")).intValue();
        String rank = (String) row.get("rank");

        PlayerData data = new PlayerData(uuid, name, money, playtime, rank);
        PlayerDataHandler.getInstance().setData(data);
    }
}
