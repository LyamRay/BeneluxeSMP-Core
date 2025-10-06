package me.lyamray.bnsmpcore.listeners.player;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.ChatMessages;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class AsyncPlayerChatListener implements Listener {

    private static final Map<String, ChatMessages> RANK_MESSAGE_MAP = Map.of(
            "Overlever", ChatMessages.OVERLEVER_RANK_CHATMESSAGE,
            "Avonturier", ChatMessages.AVONTURIER_RANK_CHATMESSAGE
    );

    @EventHandler
    public void playerSendsChatMessage(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!playerHasData(player)) {
            kickPlayer(player);
            return;
        }

        String rank = getPlayerRank(player);
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
                return MiniMessage.deserializeMessage(rankMessage.getMessage(player, part1, part2));
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
        player.kick(MiniMessage.deserializeMessage(
                ChatMessages.PLAYER_HASNT_GOT_DATA.getMessage(player, "", "")
        ));
    }
}
