package me.lyamray.bnsmpcore.database.load;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.warps.WarpsData;
import me.lyamray.bnsmpcore.data.warps.WarpsDataHandler;

import java.util.Map;

@Slf4j
public class DatabaseWarpsLoader extends AbstractDatabaseLoader {
    @Getter
    private static final DatabaseWarpsLoader instance = new DatabaseWarpsLoader();

    @Override
    public String getTableName() {
        return "warps";
    }

    @Override
    protected void handleRow(Map<String, Object> row) {
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
}
