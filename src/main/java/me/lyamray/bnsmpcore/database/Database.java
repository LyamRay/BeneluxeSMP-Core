package me.lyamray.bnsmpcore.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.database.load.DatabaseFriendsLoader;
import me.lyamray.bnsmpcore.database.load.DatabaseHomesLoader;
import me.lyamray.bnsmpcore.database.load.DatabasePlayerLoader;
import me.lyamray.bnsmpcore.database.load.DatabaseWarpsLoader;
import me.lyamray.bnsmpcore.database.save.DatabaseFriendsSaver;
import me.lyamray.bnsmpcore.database.save.DatabaseHomesSaver;
import me.lyamray.bnsmpcore.database.save.DatabasePlayerSaver;
import me.lyamray.bnsmpcore.database.save.DatabaseWarpsSaver;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Database {

    @Getter
    private static final Database instance = new Database();
    private Connection connection;

    public void setupDatabase() {
        BeneluxeSMPCore beneluxeSMPCore = BeneluxeSMPCore.getInstance();
        Path dataFolder = beneluxeSMPCore.getDataFolder().toPath();

        try {
            Files.createDirectories(dataFolder);
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.resolve("BeneluxeSMPCore.db"));
            initializeTables();
            log.info("Database connected!");
        } catch (IOException | SQLException e) {
            log.warn("Database setup failed: {}", e.getMessage());
            Bukkit.getPluginManager().disablePlugin(beneluxeSMPCore);
            throw new RuntimeException(e);
        }
    }

    private void initializeTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    money INTEGER DEFAULT 0,
                    playtime INTEGER DEFAULT 0,
                    rank TEXT DEFAULT 'speler'
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS friends (
                    player_uuid TEXT NOT NULL,
                    friend_uuid TEXT NOT NULL,
                    PRIMARY KEY (player_uuid, friend_uuid),
                    FOREIGN KEY (player_uuid) REFERENCES players(uuid),
                    FOREIGN KEY (friend_uuid) REFERENCES players(uuid)
                );
            """);

            stmt.execute("""
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
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS warps (
                    name TEXT PRIMARY KEY,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    z INTEGER NOT NULL,
                    world TEXT NOT NULL,
                    required_rank TEXT DEFAULT 'speler'
                );
            """);
        }
    }
    
    public void add(String table, Map<String, Object> values) throws SQLException {
        String columns = String.join(", ", values.keySet());
        String placeholders = String.join(", ", Collections.nCopies(values.size(), "?"));
        executeUpdate("INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")", values.values().toArray());
    }

    public List<Map<String, Object>> get(String table, String whereClause, Object... params) throws SQLException {
        return executeQuery("SELECT * FROM " + table + (whereClause != null && !whereClause.isEmpty() ? " WHERE " + whereClause : ""), params);
    }

    public void set(String table, Map<String, Object> updates, String whereClause, Object... whereParams) throws SQLException {
        List<Object> allParams = new ArrayList<>(updates.values());
        allParams.addAll(Arrays.asList(whereParams));
        String setClause = String.join(", ", updates.keySet().stream().map(k -> k + " = ?").toList());
        executeUpdate("UPDATE " + table + " SET " + setClause + (whereClause != null && !whereClause.isEmpty() ? " WHERE " + whereClause : ""), allParams.toArray());
    }

    public void remove(String table, String whereClause, Object... params) throws SQLException {
        executeUpdate("DELETE FROM " + table + (whereClause != null && !whereClause.isEmpty() ? " WHERE " + whereClause : ""), params);
    }

    public boolean exists(String table, String whereClause, Object... params) throws SQLException {
        String sql = "SELECT EXISTS(SELECT 1 FROM " + table + " WHERE " + whereClause + " LIMIT 1)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        }
    }

    private void executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindParams(ps, params);
            ps.executeUpdate();
        }
    }

    private List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = prepareStatement(sql, params);
             ResultSet rs = ps.executeQuery()) {
            return mapResultSet(rs);
        }
    }

    private PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        bindParams(ps, params);
        return ps;
    }

    private List<Map<String, Object>> mapResultSet(ResultSet rs) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        while (rs.next()) {
            results.add(mapRow(rs, meta, columnCount));
        }

        return results;
    }

    private Map<String, Object> mapRow(ResultSet rs, ResultSetMetaData meta, int columnCount) throws SQLException {
        Map<String, Object> row = new HashMap<>(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            row.put(meta.getColumnName(i), rs.getObject(i));
        }
        return row;
    }
    
    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
    }

    public void loadAllData() {
        DatabaseFriendsLoader.getInstance().loadFriendsAsync(() -> log.info("Alle friends-data is succesvol ingeladen!"));
        DatabaseHomesLoader.getInstance().loadHomesAsync(() -> log.info("Alle homes-data is succesvol ingeladen!"));
        DatabasePlayerLoader.getInstance().loadPlayersAsync(() -> log.info("Alle player-data is succesvol ingeladen!"));
        DatabaseWarpsLoader.getInstance().loadWarpsAsync(() -> log.info("Alle warps-data is succesvol ingeladen!"));
    }

    public void saveAllData() {
        DatabaseFriendsSaver.getInstance().saveAllFriends();
        log.info("Alle friends-data is succesvol opgeslagen!");

        DatabaseHomesSaver.getInstance().saveAllHomes();
        log.info("Alle homes-data is succesvol opgeslagen!");

        DatabasePlayerSaver.getInstance().saveAllPlayers();
        log.info("Alle player-data is succesvol opgeslagen!");

        DatabaseWarpsSaver.getInstance().saveAllWarps();
        log.info("Alle warps-data is succesvol opgeslagen!");
    }
}