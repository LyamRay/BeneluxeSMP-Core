package me.lyamray.bnsmpcore.data.friends;

import lombok.Getter;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FriendsDataHandler {

    @Getter
    private static final FriendsDataHandler instance = new FriendsDataHandler();

    private final Map<UUID, Set<UUID>> friendDataCache = new ConcurrentHashMap<>();

    public void setFriends(UUID player, Set<UUID> friends) {
        friendDataCache.put(player, ConcurrentHashMap.newKeySet(friends.size()));
        friendDataCache.get(player).addAll(friends);
    }

    public Set<UUID> getFriends(UUID player) {
        return friendDataCache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet());
    }

    public void addFriend(UUID player, UUID friend) {
        friendDataCache.computeIfAbsent(player, k -> ConcurrentHashMap.newKeySet()).add(friend);
    }

    public void removeFriend(UUID player, UUID friend) {
        Set<UUID> friends = friendDataCache.get(player);
        if (friends != null) friends.remove(friend);
    }

    public boolean hasFriend(UUID player, UUID friend) {
        Set<UUID> friends = friendDataCache.get(player);
        return friends != null && friends.contains(friend);
    }
}
