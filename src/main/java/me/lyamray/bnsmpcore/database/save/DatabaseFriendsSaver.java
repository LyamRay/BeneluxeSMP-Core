package me.lyamray.bnsmpcore.database.save;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.friends.FriendsDataHandler;

import java.util.*;

public class DatabaseFriendsSaver extends AbstractDatabaseSaver {

    @Getter
    private static final DatabaseFriendsSaver instance = new DatabaseFriendsSaver();

    @Override
    public String getTableName() {
        return "friends";
    }

    @Override
    protected Iterable<Map<String, Object>> getAllEntriesToSave() {
        List<Map<String, Object>> entries = new ArrayList<>();
        FriendsDataHandler.getInstance().getCacheMap().forEach((player, friends) -> {
            for (UUID friend : friends) {
                entries.add(Map.of(
                        "player_uuid", player.toString(),
                        "friend_uuid", friend.toString()
                ));
            }
        });
        return entries;
    }
}
