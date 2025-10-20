package me.lyamray.bnsmpcore.utils.handlers.scoreboard;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.GlobalMessages;
import me.lyamray.bnsmpcore.utils.messages.ScoreboardMessages;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

@Slf4j
public class ScoreboardHandler {

    @Getter
    private static final ScoreboardHandler instance = new ScoreboardHandler();

    private final BeneluxeSMPCore plugin = BeneluxeSMPCore.getInstance();
    private BukkitTask scoreboardTask;

    private ScoreboardHandler() {}

    public void startScoreboardTask(long intervalTicks) {
        if (scoreboardTask != null && !scoreboardTask.isCancelled()) {
            log.warn("Scoreboard task already running!");
            return;
        }

        scoreboardTask = Bukkit.getScheduler().runTaskTimer(plugin, this::updateAllScoreboards, 0L, intervalTicks);
        log.info("ScoreboardHandler task started with interval {} ticks.", intervalTicks);
    }

    public void stopScoreboardTask() {
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
            scoreboardTask = null;
            log.info("ScoreboardHandler task stopped.");
        }
    }

    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboardFor(player);
        }
    }

    public void updateScoreboardFor(Player player) {
        if (!PlayerDataHandler.getInstance().has(player.getUniqueId())) return;

        PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());

        if (!data.isScoreboardEnabled()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }

        int claimBlocks = data.getClaimBlocks();
        int money = data.getMoney();

        Ranks rankEnum = parseRank(data.getRank());
        String rank = rankEnum != null ? rankEnum.getMessage() : "<gray>Unknown</gray>";

        var lines = ScoreboardMessages.DEFAULT_SCOREBOARD.getLines(player, claimBlocks, money, rank);

        ScoreboardManager.getInstance().setScoreboard(
                player,
                GlobalMessages.BENELUXE_TITLE.getMessage(),
                lines
        );
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
