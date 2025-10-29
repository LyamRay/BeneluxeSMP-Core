package me.lyamray.beneluxesmpcore.listeners.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.lyamray.beneluxesmpcore.BeneluxeSMPCore;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.ChatMessages;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import me.lyamray.beneluxesmpcore.utils.ranks.Ranks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class AsyncPlayerChatListener implements Listener {

    private static final Map<Ranks, ChatMessages> RANK_MESSAGE_MAP = Map.of(
            Ranks.OVERLEVER, ChatMessages.OVERLEVER_RANK_CHATMESSAGE,
            Ranks.VERKENNER, ChatMessages.VERKENNER_RANK_CHATMESSAGE,
            Ranks.AVONTURIER, ChatMessages.AVONTURIER_RANK_CHATMESSAGE,
            Ranks.OVERLEVER_PLUS, ChatMessages.OVERLEVER_PLUS_RANK_CHATMESSAGE,
            Ranks.VETERAAN, ChatMessages.VETERAAN_RANK_CHATMESSAGE,
            Ranks.HELPER, ChatMessages.HELPER_RANK_CHATMESSAGE,
            Ranks.MODERATOR, ChatMessages.MODERATOR_RANK_CHATMESSAGE,
            Ranks.ADMIN, ChatMessages.ADMIN_RANK_CHATMESSAGE
    );

    @EventHandler
    public void playerSendsChatMessage(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!playerHasData(player)) {
            kickPlayer(player);
            return;
        }

        String rankString = getPlayerRank(player);
        Ranks rank = parseRank(rankString);
        if (rank == null) {
            kickPlayer(player);
            return;
        }

        ChatMessages rankMessage = RANK_MESSAGE_MAP.get(rank);

        event.renderer((source, displayName, msg, viewer) -> {
            String rawMessage = msg instanceof TextComponent tc
                    ? tc.content()
                    : msg.toString();

            int mid = rawMessage.length() / 2;
            String part1 = rawMessage.substring(0, mid);
            String part2 = rawMessage.substring(mid);

            if (rankMessage != null) {
                return MiniMessage.deserializeMessage(rankMessage.getMessage(rank, player, part1, part2));
            } else {
                kickPlayer(player);
                return Component.empty();
            }
        });
    }

    private boolean playerHasData(Player player) {
        return PlayerDataHandler.getInstance().has(player.getUniqueId());
    }

    private String getPlayerRank(Player player) {
        return PlayerDataHandler.getInstance().getData(player.getUniqueId()).getRank();
    }

    private void kickPlayer(Player player) {
        Bukkit.getScheduler().runTask(BeneluxeSMPCore.getInstance(), () -> player.kick(MiniMessage.deserializeMessage(
                ChatMessages.PLAYER_HASNT_GOT_DATA.getMessage(Ranks.OVERLEVER, player, "", "")
        )));
    }

    private Ranks parseRank(String rankString) {
        if (rankString == null) return null;
        try {
            return Ranks.valueOf(rankString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
