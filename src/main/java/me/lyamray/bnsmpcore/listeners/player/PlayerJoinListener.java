package me.lyamray.bnsmpcore.listeners.player;

import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.JoinMessages;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoins(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean playerHasPlayed = PlayerDataHandler.getInstance().has(player.getUniqueId());

        Component message = MiniMessage.deserializeMessage(
                playerHasPlayed
                        ? JoinMessages.PLAYER_JOIN_MESSAGE.getMessage(player)
                        : JoinMessages.PLAYER_FIRST_TIME_JOIN_MESSAGE.getMessage(player)
        );

        player.sendMessage(message);

        if (!playerHasPlayed) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = new PlayerData(uuid, player.getName(), 5000, 0, "Overlever");
            PlayerDataHandler.getInstance().addData(playerData);
        }
    }
}
