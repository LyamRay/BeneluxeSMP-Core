package me.lyamray.bnsmpcore.listeners.player;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void playerLeft(PlayerQuitEvent event) {
        event.quitMessage(Component.empty());

    }
}
