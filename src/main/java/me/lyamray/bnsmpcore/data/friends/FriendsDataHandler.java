package me.lyamray.bnsmpcore.data.friends;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;
import me.lyamray.bnsmpcore.data.warps.WarpsData;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FriendsDataHandler extends AbstractDataHandler<Set<UUID>> {

    @Getter
    private static final FriendsDataHandler instance = new FriendsDataHandler();

    public void addFriend(Integer player, UUID friend) {
        cache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(friend);
    }

    public void removeFriend(UUID player, UUID friend) {
        WarpsData friends = cache.get(player);
        if (friends != null) friends.remove(friend);
    }

    public boolean hasFriend(UUID player, UUID friend) {
        WarpsData friends = cache.get(player);
        return friends != null && friends.contains(friend);
    }
}
