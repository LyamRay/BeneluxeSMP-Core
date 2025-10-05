package me.lyamray.bnsmpcore.database.save;

import me.lyamray.bnsmpcore.database.Database;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractDatabaseSaver {
    public abstract String getTableName();
    protected abstract Iterable<Map<String, Object>> getAllEntriesToSave() throws SQLException;

    public void saveAll() {
        try {
            for (Map<String, Object> entry : getAllEntriesToSave()) {
                Database.getInstance().add(getTableName(), entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
