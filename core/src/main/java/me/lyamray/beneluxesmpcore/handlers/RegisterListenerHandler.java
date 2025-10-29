package me.lyamray.beneluxesmpcore.handlers;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.beneluxesmpcore.BeneluxeSMPCore;
import me.lyamray.beneluxesmpcore.listeners.player.AsyncPlayerChatListener;
import me.lyamray.beneluxesmpcore.listeners.player.PlayerJoinListener;
import me.lyamray.beneluxesmpcore.listeners.player.PlayerLeaveListener;
import me.lyamray.beneluxesmpcore.listeners.player.PlayerMoveListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.List;

@Slf4j
@UtilityClass
public class RegisterListenerHandler {

    public void registerAll() {
        List<Class<? extends Listener>> listeners = List.of(
                PlayerJoinListener.class,
                PlayerLeaveListener.class,
                AsyncPlayerChatListener.class,
                PlayerMoveListener.class
        );

        for (Class<? extends Listener> clazz : listeners) {
            try {
                Listener listener = clazz.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(listener, BeneluxeSMPCore.getInstance());
                log.info("Registered listener: {}", clazz.getSimpleName());
            } catch (Exception e) {
                log.warn("Failed to register listener: {}", clazz.getName(), e);
            }
        }
    }
}
