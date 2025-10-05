package me.lyamray.bnsmpcore.database.load;

import me.lyamray.bnsmpcore.database.Database;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class AbstractDatabaseLoader {


    public abstract String getTableName();

    protected abstract void handleRow(Map<String, Object> row) throws SQLException;

    public void load() throws SQLException {
        List<Map<String, Object>> results = Database.getInstance().get(getTableName(), null);
        for (Map<String, Object> row : results) {
            try {
                handleRow(row);
            } catch (Exception e) {
                System.err.println("Failed to handle row in table " + getTableName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}