package me.lyamray.bnsmpcore.utils.manager;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.listeners.player.AsyncPlayerChatListener;
import me.lyamray.bnsmpcore.listeners.player.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.List;

@Slf4j
@UtilityClass
public class RegisterListenerManager {

    public void registerAll() {
        List<Class<? extends Listener>> listeners = List.of(
                PlayerJoinListener.class,
                AsyncPlayerChatListener.class
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
