package me.lyamray.bnsmpcore.listeners.player;

import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.JoinMessages;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import me.lyamray.bnsmpcore.utils.messages.TabMessages;
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
    public void playerJoins(PlayerJoinEvent event) {
        event.joinMessage(Component.text(""));

        Player player = event.getPlayer();
        boolean playerHasPlayed = PlayerDataHandler.getInstance().has(player.getUniqueId());

        welcome(player, playerHasPlayed);

        if (!playerHasPlayed) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = new PlayerData(uuid, player.getName(), 5000, 0, "Overlever");
            PlayerDataHandler.getInstance().addData(playerData);
        }
    }

    private void welcome(Player player, boolean playerHasPlayed) {
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

        int aantalSpelers = Bukkit.getOnlinePlayers().size();
        player.sendPlayerListHeader(MiniMessage.deserializeMessage(TabMessages.HEADER.getMessage(aantalSpelers)));
        player.sendPlayerListFooter(MiniMessage.deserializeMessage(TabMessages.FOOTER.getMessage(0)));
    }
}
