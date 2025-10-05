package me.lyamray.bnsmpcore.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.database.load.AbstractDatabaseLoader;
import me.lyamray.bnsmpcore.database.load.DatabaseFriendsLoader;
import me.lyamray.bnsmpcore.database.save.*;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Database {

    @Getter
    private static final Database instance = new Database();
    private Connection connection;

    private final List<AbstractDatabaseLoader> loaders = Arrays.asList(
            DatabaseFriendsLoader.getInstance()
    );

    private final List<AbstractDatabaseSaver> savers = Arrays.asList(
            DatabaseFriendsSaver.getInstance()
    );

    public void setupDatabase() {
        BeneluxeSMPCore plugin = BeneluxeSMPCore.getInstance();
        Path dbPath = plugin.getDataFolder().toPath().resolve("BeneluxeSMPCore.db");

        try {
            Files.createDirectories(dbPath.getParent());
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.setAutoCommit(true);
            initializeTables();
            log.info("Database initialized successfully.");
        } catch (IOException | SQLException e) {
            log.error("Database setup failed: {}", e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    private void initializeTables() throws SQLException {
        Map<String, String> tables = Map.of(
                "players", """
                            CREATE TABLE IF NOT EXISTS players (
                                uuid TEXT PRIMARY KEY,
                                name TEXT NOT NULL,
                                money INTEGER DEFAULT 0,
                                playtime INTEGER DEFAULT 0,
                                rank TEXT DEFAULT 'speler'
                            );
                        """,
                "friends", """
                            CREATE TABLE IF NOT EXISTS friends (
                                player_uuid TEXT NOT NULL,
                                friend_uuid TEXT NOT NULL,
                                PRIMARY KEY (player_uuid, friend_uuid),
                                FOREIGN KEY (player_uuid) REFERENCES players(uuid),
                                FOREIGN KEY (friend_uuid) REFERENCES players(uuid)
                            );
                        """,
                "homes", """
                            CREATE TABLE IF NOT EXISTS homes (
                                player_uuid TEXT NOT NULL,
                                home_name TEXT NOT NULL,
                                x INTEGER NOT NULL,
                                y INTEGER NOT NULL,
                                z INTEGER NOT NULL,
                                world TEXT NOT NULL,
                                PRIMARY KEY (player_uuid, home_name),
                                FOREIGN KEY (player_uuid) REFERENCES players(uuid)
                            );
                        """,
                "warps", """
                            CREATE TABLE IF NOT EXISTS warps (
                                name TEXT PRIMARY KEY,
                                x INTEGER NOT NULL,
                                y INTEGER NOT NULL,
                                z INTEGER NOT NULL,
                                world TEXT NOT NULL,
                                required_rank TEXT DEFAULT 'speler'
                            );
                        """
        );

        try (Statement stmt = connection.createStatement()) {
            for (String ddl : tables.values()) {
                stmt.execute(ddl);
            }
        }
    }

    public void loadAllData() {
        loaders.forEach(loader -> {
            try {
                loader.load();
                log.info("{} data loaded successfully.", loader.getTableName());
            } catch (Exception e) {
                log.warn("Failed to load {} data: {}", loader.getTableName(), e.getMessage());
            }
        });
    }

    public void saveAllData() {
        savers.forEach(saver -> {
            try {
                saver.saveAll();
                log.info("{} data saved successfully.", saver.getTableName());
            } catch (Exception e) {
                log.warn("Failed to save {} data: {}", saver.getTableName(), e.getMessage());
            }
        });
    }

    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("Database connection closed.");
            }
        } catch (SQLException e) {
            log.error("Error closing database connection: {}", e.getMessage(), e);
        }
    }

    public void add(String table, Map<String, Object> values) throws SQLException {
        String columns = String.join(", ", values.keySet());
        String placeholders = values.keySet().stream().map(k -> "?").collect(Collectors.joining(", "));
        executeUpdate("INSERT OR REPLACE INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")", values.values().toArray());
    }

    public List<Map<String, Object>> get(String table, String whereClause, Object... params) throws SQLException {
        String sql = "SELECT * FROM " + table + (isEmpty(whereClause) ? "" : " WHERE " + whereClause);
        return executeQuery(sql, params);
    }

    public void update(String table, Map<String, Object> updates, String whereClause, Object... whereParams) throws SQLException {
        String setClause = updates.keySet().stream().map(k -> k + " = ?").collect(Collectors.joining(", "));
        Object[] params = concat(updates.values().toArray(), whereParams);
        String sql = "UPDATE " + table + " SET " + setClause + (isEmpty(whereClause) ? "" : " WHERE " + whereClause);
        executeUpdate(sql, params);
    }

    public void delete(String table, String whereClause, Object... params) throws SQLException {
        String sql = "DELETE FROM " + table + (isEmpty(whereClause) ? "" : " WHERE " + whereClause);
        executeUpdate(sql, params);
    }

    public boolean exists(String table, String whereClause, Object... params) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE " + whereClause + " LIMIT 1";
        try (PreparedStatement ps = prepareStatement(sql, params);
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    private void executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepareStatement(sql, params)) {
            ps.executeUpdate();
        }
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepareStatement(sql, params);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            List<Map<String, Object>> result = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
            return result;
        }
    }

    private PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }

    private static Object[] concat(Object[] first, Object[] second) {
        Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
