package me.lyamray.beneluxesmpcore.listeners.player;

import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.handlers.passenger.PlayerNameHandler;
import me.lyamray.beneluxesmpcore.handlers.scoreboard.ScoreboardHandler;
import me.lyamray.beneluxesmpcore.handlers.tab.TabHandler;
import me.lyamray.beneluxesmpcore.utils.messages.PlayerMessages;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
        UUID uuid = player.getUniqueId();
        PlayerData playerData = new PlayerData(uuid, player.getName(), 5000, 0, "OVERLEVER", true, 0, 0);
        PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());

        if (!playerHasPlayed) {
            PlayerDataHandler.getInstance().addData(playerData);

            Location location = new Location(Bukkit.getWorld("spawn"), 0, 100, 0).toCenterLocation();
            location.setYaw(180);
            location.setPitch(0);
            player.getPassengers().clear();
            player.teleportAsync(location);
            PlayerNameHandler.getInstance().updateNameFor(player, data);
        }

        welcomeMesssages(player, playerHasPlayed);
        PlayerNameHandler.getInstance().updateNameFor(player, data);
        TabHandler.getInstance().updateTabForPlayer(player, Bukkit.getOnlinePlayers().size());
        ScoreboardHandler.getInstance().updateScoreboardFor(player);
        updateData(player);
    }

    private void welcomeMesssages(Player player, boolean playerHasPlayed) {
        Component message = MiniMessage.deserializeMessage(
                playerHasPlayed
                        ? PlayerMessages.PLAYER_JOIN_MESSAGE.getMessage(player)
                        : PlayerMessages.PLAYER_FIRST_TIME_JOIN_MESSAGE.getMessage(player)
        );
        player.sendMessage(message);

        Component title = MiniMessage.deserializeMessage(
                playerHasPlayed
                        ? PlayerMessages.TITLE_HAS_JOINED.getMessage(player)
                        : PlayerMessages.TITLE_HAS_NOT_JOINED.getMessage(player)
        );

        player.showTitle(Title.title(
                title,
                MiniMessage.deserializeMessage(PlayerMessages.SUBTITLE.getMessage(player)),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))
        ));
    }
    private void updateData(Player player) {
        PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());
        data.setName(player.getName());
        PlayerDataHandler.getInstance().setData(data);
    }
}
