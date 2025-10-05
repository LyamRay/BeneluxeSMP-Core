package me.lyamray.bnsmpcore.database.load;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.data.friends.FriendsDataHandler;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class DatabaseFriendsLoader extends AbstractDatabaseLoader {
    @Getter
    private static final DatabaseFriendsLoader instance = new DatabaseFriendsLoader();

    @Override
    public String getTableName() {
        return "friends";
    }

    @Override
    protected void handleRow(Map<String, Object> row) {
        Integer player = UUID.fromString((String) row.get("player_uuid"));
        UUID friend = UUID.fromString((String) row.get("friend_uuid"));
        FriendsDataHandler.getInstance().addFriend(player, friend);
    }
}
