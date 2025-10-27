package me.lyamray.beneluxesmpcore.handlers.tab;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.beneluxesmpcore.BeneluxeSMPCore;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import me.lyamray.beneluxesmpcore.utils.messages.TabMessages;
import me.lyamray.beneluxesmpcore.utils.ranks.Ranks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@Slf4j
public class TabHandler {

    @Getter
    private static final TabHandler instance = new TabHandler();

    private BukkitTask tabTask;
    private final BeneluxeSMPCore plugin = BeneluxeSMPCore.getInstance();

    private TabHandler() {}

    public void startTabTask(long intervalTicks) {
        if (tabTask != null && !tabTask.isCancelled()) {
            log.warn("Tab task is already running!");
            return;
        }

        tabTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAll, 0L, intervalTicks);
        log.info("TabHandler task started with interval {} ticks.", intervalTicks);
    }

    public void stopTabTask() {
        if (tabTask != null) {
            tabTask.cancel();
            tabTask = null;
            log.info("TabHandler task stopped.");
        }
    }

    public void updateAll() {
        int onlineCount = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            updateTabForPlayer(player, onlineCount);
        }
    }

    public void updateTabForPlayer(Player player, int onlineCount) {
        try {
            // Update header and footer
            player.sendPlayerListHeader(MiniMessage.deserializeMessage(TabMessages.HEADER.getMessage(onlineCount)));
            player.sendPlayerListFooter(MiniMessage.deserializeMessage(TabMessages.FOOTER.getMessage(onlineCount)));

            PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());
            if (data != null) {
                updateNameFor(player, data);
            }
        } catch (Exception e) {
            log.error("Failed to update tab for player {}", player.getName(), e);
        }
    }

    public void updateNameFor(Player player, PlayerData playerData) {
        if (playerData == null) return;

        Ranks rank = parseRank(playerData.getRank());
        if (rank == null) rank = Ranks.OVERLEVER;

        String playerMini = TabMessages.PLAYER_ENTRY.getPlayerMessage(rank, player);
        Component nameComponent = MiniMessage.deserializeMessage(playerMini);

        int weight = rank.getWeight();

        player.setPlayerListOrder(weight);
        player.playerListName(nameComponent);
        player.displayName(nameComponent);
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
