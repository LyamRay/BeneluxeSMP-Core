package me.lyamray.bnsmpcore.listeners.player;

import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.manager.scoreboard.ScoreboardManager;
import me.lyamray.bnsmpcore.utils.messages.*;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
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

        if (!playerHasPlayed) {
            UUID uuid = player.getUniqueId();
            PlayerData playerData = new PlayerData(uuid, player.getName(), 5000, 0, "Overlever", true, 0);
            PlayerDataHandler.getInstance().addData(playerData);
        }

        welcomeMesssages(player, playerHasPlayed);
        tabMessages(player);
        scoreBoardMessages(player);
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

    private void tabMessages(Player player) {
        int aantalSpelers = Bukkit.getOnlinePlayers().size();
        player.sendPlayerListHeader(MiniMessage.deserializeMessage(TabMessages.HEADER.getMessage(aantalSpelers)));
        player.sendPlayerListFooter(MiniMessage.deserializeMessage(TabMessages.FOOTER.getMessage(0)));
    }

    private void scoreBoardMessages(Player player) {
        if (!PlayerDataHandler.getInstance().has(player.getUniqueId())) return;
        PlayerData playerData = PlayerDataHandler.getInstance().getData(player.getUniqueId());

        if (!playerData.isScoreboardEnabled()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }

        int claimBlocks = playerData.getClaimBlocks();
        int money = playerData.getMoney();

        String rank = getString(playerData);

        ScoreboardManager.getInstance().setScoreboard(
                player,
                GlobalMessages.SCOREBOARD_TITLE.getMessage(),
                ScoreboardMessages.DEFAULT_SCOREBOARD.getLines(player, claimBlocks, money, rank)
        );
    }

    private static String getString(PlayerData playerData) {
        String rankString = playerData.getRank();
        String rank;
        switch (rankString.toUpperCase()) {
            case "OVERLEVER" -> rank = Ranks.OVERLEVER.getMessage();
            case "VERKENNER" -> rank = Ranks.VERKENNER.getMessage();
            case "AVONTURIER" -> rank = Ranks.AVONTURIER.getMessage();
            case "OVERLEVER_PLUS" -> rank = Ranks.OVERLEVER_PLUS.getMessage();
            case "VETERAAN" -> rank = Ranks.VETERAAN.getMessage();
            case "HELPER" -> rank = Ranks.HELPER.getMessage();
            case "MODERATOR" -> rank = Ranks.MODERATOR.getMessage();
            case "ADMIN" -> rank = Ranks.ADMIN.getMessage();
            default -> rank = "Unknown Rank";
        }
        return rank;
    }
}
