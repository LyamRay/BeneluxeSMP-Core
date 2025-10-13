package me.lyamray.bnsmpcore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.commands.data.dataCommand;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.manager.RegisterListenerManager;
import me.lyamray.bnsmpcore.utils.manager.scoreboard.ScoreboardHandler;
import me.lyamray.bnsmpcore.utils.manager.tab.TabHandler;
import org.bukkit.plugin.java.JavaPlugin;

@Slf4j
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
        getCommand("data").setExecutor(new dataCommand());
        TabHandler.getInstance().startTabTask(20 * 5L);
        ScoreboardHandler.getInstance().startScoreboardTask(20 * 5L);
    }

    @Override
    public void onDisable() {
        Database.getInstance().saveAllData();
        Database.getInstance().shutdown();
        TabHandler.getInstance().stopTabTask();
        ScoreboardHandler.getInstance().stopScoreboardTask();
    }
}
