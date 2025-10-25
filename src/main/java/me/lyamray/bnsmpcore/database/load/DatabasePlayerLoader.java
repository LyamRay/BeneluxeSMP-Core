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
        long money = ((Number) row.get("money")).intValue();
        long playtime = ((Number) row.get("playtime")).intValue();
        String rank = (String) row.get("rank");
        int sbInt = ((Number) row.get("scoreboardEnabled")).intValue();
        boolean scoreboardEnabled = sbInt != 0;
        long claimBlocks = ((Number) row.get("claimBlocks")).intValue();
        int credits = ((Number)row.get("credits")).intValue();

        PlayerData data = new PlayerData(uuid, name, money, playtime, rank, scoreboardEnabled, claimBlocks, credits);
        PlayerDataHandler.getInstance().setData(data);
    }
}
