package me.lyamray.bnsmpcore.listeners.player;

import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.manager.scoreboard.ScoreboardHandler;
import me.lyamray.bnsmpcore.utils.manager.tab.TabHandler;
import me.lyamray.bnsmpcore.utils.messages.JoinMessages;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void playerJoined(PlayerJoinEvent event) {
        event.joinMessage(Component.empty());

        Player player = event.getPlayer();
        boolean playerHasPlayed = PlayerDataHandler.getInstance().has(player.getUniqueId());

        if (!playerHasPlayed) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = new PlayerData(uuid, player.getName(), 5000, 0, "Overlever", true, 0);
            PlayerDataHandler.getInstance().addData(playerData);
        }

        welcomeMesssages(player, playerHasPlayed);
        TabHandler.getInstance().updateTabForPlayer(player, Bukkit.getOnlinePlayers().size());
        ScoreboardHandler.getInstance().updateScoreboardFor(player);
    }

    private void welcomeMesssages(Player player, boolean playerHasPlayed) {
        Component message = MiniMessage.deserializeMessage(
                playerHasPlayed
                        ? JoinMessages.PLAYER_JOIN_MESSAGE.getMessage(player)
                        : JoinMessages.PLAYER_FIRST_TIME_JOIN_MESSAGE.getMessage(player)
        );
        player.sendMessage(message);

        Component title = MiniMessage.deserializeMessage(
                playerHasPlayed
                        ? JoinMessages.TITLE_HAS_JOINED.getMessage(player)
                        : JoinMessages.TITLE_HAS_NOT_JOINED.getMessage(player)
        );

        player.showTitle(Title.title(
                title,
                MiniMessage.deserializeMessage(JoinMessages.SUBTITLE.getMessage(player)),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
        ));
    }
}
