package me.lyamray.bnsmpcore.database.save;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.friends.FriendsDataHandler;
import me.lyamray.bnsmpcore.database.Database;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DatabaseFriendsSaver {

    @Getter
    private static final DatabaseFriendsSaver instance = new DatabaseFriendsSaver();
    private final Database database = Database.getInstance();

    public void saveAllFriends() {
        FriendsDataHandler.getInstance().getFriendDataCache().forEach((player, friends) -> {
            for (UUID friend : friends) {
                saveFriend(player, friend);
            }
        });
    }

    private void saveFriend(UUID player, UUID friend) {
        try {
            boolean exists = database.exists("friends", "player_uuid = ? AND friend_uuid = ?", player.toString(), friend.toString());
            if (!exists) {
                database.add("friends", Map.of(
                        "player_uuid", player.toString(),
                        "friend_uuid", friend.toString()
                ));
            }
        } catch (SQLException e) {
            log.warn("Failed to save friend {} for {}: {}", friend, player, e.getMessage());
        }
    }
}
