package me.lyamray.beneluxesmpcore.listeners.player;

import me.lyamray.beneluxesmpcore.handlers.passenger.PlayerNameHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerSneakListener implements Listener {

    private static final byte SNEAK_OPACITY = 100;
    private static final byte DEFAULT_OPACITY = (byte) 255;

    @EventHandler
    private void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Pair<TextDisplay, String> pair = PlayerNameHandler.getInstance().getNameDisplays().get(player);

        if (pair == null) {
            return;
        }
        TextDisplay display = pair.getLeft();
        if (display == null || display.isDead()) {
            return;
        }

        adjustDisplayForSneak(player, display, event.isSneaking());
    }

    private void adjustDisplayForSneak(Player player, TextDisplay display, boolean isSneaking) {
        if (isSneaking) {
            display.setTextOpacity(SNEAK_OPACITY);
        } else {
            display.setTextOpacity(DEFAULT_OPACITY);
        }

        PlayerNameHandler.getInstance().getNameDisplays().put(player, Pair.of(display, PlayerNameHandler.getInstance().getNameDisplays().get(player).getRight()));
        PlayerNameHandler.getInstance().updateNameFor(player);
    }
}
