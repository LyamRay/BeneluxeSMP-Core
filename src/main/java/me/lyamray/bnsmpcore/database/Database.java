package me.lyamray.bnsmpcore.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.database.load.*;
import me.lyamray.bnsmpcore.database.save.*;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Database {

    @Getter
    private static final Database instance = new Database();

    private Connection connection;

    private final List<AbstractDatabaseLoader> loaders = Arrays.asList(
            DatabaseFriendsLoader.getInstance(),
            DatabaseHomesLoader.getInstance(),
            DatabasePlayerLoader.getInstance(),
            DatabaseWarpsLoader.getInstance()
    );

    private final List<AbstractDatabaseSaver> savers = Arrays.asList(
            DatabaseFriendsSaver.getInstance(),
            DatabaseHomesSaver.getInstance(),
            DatabasePlayerSaver.getInstance(),
            DatabaseWarpsSaver.getInstance()
    );

    private Database(String dbPath) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        connection.setAutoCommit(true);
        initializeTables();
    }

    public void setupDatabase() {
        BeneluxeSMPCore plugin = BeneluxeSMPCore.getInstance();
        Path dbPath = plugin.getDataFolder().toPath().resolve("BeneluxeSMPCore.db");

        try {
            Files.createDirectories(dbPath.getParent());
            this.connection = new Database(dbPath.toString()).connection;
            log.info("Connected successfully to the database!");
        } catch (IOException | SQLException e) {
            fatalError(e, plugin);
        }
    }

    private void fatalError(Exception e, BeneluxeSMPCore plugin) {
        log.error("{}: {}", "Failed to setup database", e.getMessage(), e);
        Bukkit.getPluginManager().disablePlugin(plugin);
        throw new RuntimeException("Failed to setup database", e);
    }

    private void initializeTables() throws SQLException {
        Map<String, String> tables = Map.of(
                "players", """
                        CREATE TABLE IF NOT EXISTS players (
                            uuid TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            money INTEGER DEFAULT 0,
                            playtime INTEGER DEFAULT 0,
                            rank TEXT DEFAULT 'Overlever',
                            scoreboardEnabled INTEGER DEFAULT 1,
                            claimBlocks INTEGER DEFAULT 0
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
            tables.values().forEach(sql -> {
                try {
                    stmt.execute(sql);
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to create table: " + e.getMessage(), e);
                }
            });
        }
    }



    public void add(String table, Map<String, Object> values) {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        executeUpdate("INSERT OR REPLACE INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")", values.values().toArray());
    }

    public void update(String table, Map<String, Object> updates, String whereClause, Object... whereParams) {
        String setClause = String.join(", ", updates.keySet().stream().map(k -> k + " = ?").toList());
        Object[] params = concat(updates.values().toArray(), whereParams);
        executeUpdate("UPDATE " + table + " SET " + setClause + (isNotEmpty(whereClause) ? " WHERE " + whereClause : ""), params);
    }

    public void delete(String table, String whereClause, Object... params) {
        executeUpdate("DELETE FROM " + table + (isNotEmpty(whereClause) ? " WHERE " + whereClause : ""), params);
    }

    public boolean exists(String table, String whereClause, Object... params) {
        String sql = "SELECT 1 FROM " + table + " WHERE " + whereClause + " LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check existence: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> get(String table, String whereClause, Object... params) {
        return executeQuery("SELECT * FROM " + table + (isNotEmpty(whereClause) ? " WHERE " + whereClause : ""), params);
    }


    private void executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed: " + e.getMessage(), e);
        }
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
    }

    private List<Map<String, Object>> mapResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) row.put(meta.getColumnName(i), rs.getObject(i));
            result.add(row);
        }
        return result;
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.isBlank();
    }

    private Object[] concat(Object[] first, Object[] second) {
        Object[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public void loadAllData() {
        for (AbstractDatabaseLoader loader : loaders) {
            try {
                loader.load();
                log.info("{} data loaded successfully.", loader.getTableName());
            } catch (Exception e) {
                log.warn("Failed to load {} data: {}", loader.getTableName(), e.getMessage());
                log.warn(String.valueOf(e));
            }
        }
    }

    public void saveAllData() {
        for (AbstractDatabaseSaver saver : savers) {
            try {
                saver.saveAll();
                log.info("{} data saved successfully.", saver.getTableName());
            } catch (Exception e) {
                log.warn("Failed to save {} data: {}", saver.getTableName(), e.getMessage());
                log.warn(String.valueOf(e));
            }
        }
    }

    public void shutdown() {
        if (connection == null) return;
        try {
            connection.close();
            log.info("Database connection closed.");
        } catch (SQLException e) {
            log.error("Error closing database connection: {}", e.getMessage(), e);
        }
    }
}
