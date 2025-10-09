package me.lyamray.bnsmpcore.utils.manager.scoreboard;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardInterface {

    void setScoreboard(Player player, Component title, List<Component> lines);

    void clear(Player player);

    boolean hasScoreboard(Player player);
}