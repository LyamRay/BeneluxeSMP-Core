package me.lyamray.bnsmpcore;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.lyamray.bnsmpcore.commands.data.DataCommand;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.handlers.RegisterListenerManager;
import me.lyamray.bnsmpcore.utils.handlers.holograms.RichestPlayersHologramHandler;
import me.lyamray.bnsmpcore.utils.handlers.passenger.PlayerNameHandler;
import me.lyamray.bnsmpcore.utils.handlers.scoreboard.ScoreboardHandler;
import me.lyamray.bnsmpcore.utils.handlers.tab.TabHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BeneluxeSMPCore extends JavaPlugin {

    @Getter
    private static BeneluxeSMPCore instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        Database.getInstance().setupDatabase();
        Database.getInstance().loadAllData();

        RegisterListenerManager.registerAll();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands ->
                commands.registrar().register(DataCommand.getInstance().create(), "Admin data command!"));

        startAllTasks();
    }

    @Override
    public void onDisable() {
        Database.getInstance().saveAllData();
        Database.getInstance().shutdown();
        stopAllTasks();
    }

    private void startAllTasks() {
        TabHandler.getInstance().startTabTask(20 * 5L);
        ScoreboardHandler.getInstance().startScoreboardTask(20 * 5L);
        PlayerNameHandler.getInstance().startPassengerCheckTask(20 * 5L);
        RichestPlayersHologramHandler.getInstance().startUpdateTask(20 * 10L);
    }

    private void stopAllTasks() {
        TabHandler.getInstance().stopTabTask();
        ScoreboardHandler.getInstance().stopScoreboardTask();
        PlayerNameHandler.getInstance().stopPassengerCheckTask();
        PlayerNameHandler.getInstance().removeAll();
        RichestPlayersHologramHandler.getInstance().stopUpdateTask();
    }
}
