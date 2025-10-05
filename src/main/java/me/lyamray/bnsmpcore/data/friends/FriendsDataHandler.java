package me.lyamray.bnsmpcore.data.friends;

import lombok.Getter;
import me.lyamray.bnsmpcore.data.AbstractDataHandler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FriendsDataHandler extends AbstractDataHandler<Set<UUID>, UUID> {

    @Getter
    private static final FriendsDataHandler instance = new FriendsDataHandler();

    public Map<UUID, Set<UUID>> getCacheMap() {
        return cache;
    }

    public void addFriend(UUID player, UUID friend) {
        cache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(friend);
    }

    public void removeFriend(UUID player, UUID friend) {
        Set<UUID> friends = cache.get(player);
        if (friends != null) friends.remove(friend);
    }

    public boolean hasFriend(UUID player, UUID friend) {
        Set<UUID> friends = cache.get(player);
        return friends != null && friends.contains(friend);
    }

    public Set<UUID> getFriends(UUID player) {
        return cache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
    }
}
