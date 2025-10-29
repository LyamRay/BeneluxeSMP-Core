package me.lyamray.beneluxesmpcore.listeners.player;

import me.lyamray.beneluxesmpcore.handlers.passenger.PlayerNameHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(Component.empty());
        PlayerNameHandler.getInstance().remove(player);
    }
}
