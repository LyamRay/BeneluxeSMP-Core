package me.lyamray.bnsmpcore.listeners.player;

import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.handlers.passenger.PlayerNameHandler;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import me.lyamray.bnsmpcore.utils.messages.PlayerMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void playerMoves(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("spawn")) {
            spawnWalkLimit(player);
            return;
        }

    }

    private void spawnWalkLimit(Player player) {
        int y = player.getLocation().getBlockY();
        if (player.isOp()) return;

        if (y > 120 || y < 80) {
            player.sendMessage(MiniMessage.deserializeMessage(
                    PlayerMessages.PLAYER_MUST_NOT_WALK_HERE.getMessage(player)
            ));

            Location location = new Location(Bukkit.getWorld("spawn"), 0, 100, 0);
            location.setYaw(180);
            location.setPitch(0);
            for (Entity passenger : player.getPassengers()) {
                passenger.remove();
            }

            player.teleport(location);
            PlayerData playerData = PlayerDataHandler.getInstance().getData(player.getUniqueId());
            PlayerNameHandler.getInstance().updateNameFor(player, playerData);

        }
    }
}
