package me.lyamray.bnsmpcore.utils.manager.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class ScoreboardManager implements ScoreboardInterface {

    @Getter
    private static ScoreboardManager instance = new ScoreboardManager();
    private final Map<UUID, Scoreboard> boards = new HashMap<>();

    @Override
    public void setScoreboard(Player player, Component title, List<Component> lines) {
        if (player == null || !player.isOnline() || lines.isEmpty()) return;

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                "scoreboard",
                Criteria.DUMMY,
                title
        );

        objective.numberFormat(NumberFormat.blank());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int maxLines = Math.min(lines.size(), 15);
        int scoreValue = maxLines;

        for (int i = 0; i < maxLines; i++) {
            String placeholder = Character.toString((char) ('a' + i));
            Component line = lines.get(i);

            objective.getScore(placeholder).setScore(scoreValue--);
            objective.getScore(placeholder).customName(line);
        }

        player.setScoreboard(scoreboard);
        boards.put(player.getUniqueId(), scoreboard);
    }

    @Override
    public void clear(Player player) {
        if (player == null || !player.isOnline()) return;
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        boards.remove(player.getUniqueId());
    }

    @Override
    public boolean hasScoreboard(Player player) {
        return boards.containsKey(player.getUniqueId());
    }
}
