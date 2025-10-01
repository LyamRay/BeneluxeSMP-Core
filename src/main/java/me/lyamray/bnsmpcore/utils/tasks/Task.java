package me.lyamray.bnsmpcore.utils.tasks;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class Task {
    public void runAsync(JavaPlugin plugin, Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public void runSync(JavaPlugin plugin, Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }
}