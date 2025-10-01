package me.lyamray.bnsmpcore.database.load;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.friends.FriendsDataHandler;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.tasks.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseFriendsLoader {

    @Getter
    private static final DatabaseFriendsLoader instance = new DatabaseFriendsLoader();
    private final Database database = Database.getInstance();

    public void loadFriendsAsync(Runnable afterLoad) {
        Task.runAsync(BeneluxeSMPCore.getInstance(), () -> {
            loadFriends();
            if (afterLoad != null) Task.runSync(BeneluxeSMPCore.getInstance(), afterLoad);
        });
    }

    private void loadFriends() {
        try {
            List<Map<String, Object>> results = database.get("friends", null);
            for (Map<String, Object> row : results) {
                UUID player = UUID.fromString((String) row.get("player_uuid"));
                UUID friend = UUID.fromString((String) row.get("friend_uuid"));
                FriendsDataHandler.getInstance().addFriend(player, friend);
            }
        } catch (SQLException e) {
            log.warn("Failed to load friends: {}", e.getMessage());
            BeneluxeSMPCore.getInstance().onDisable();
        }
    }
}
