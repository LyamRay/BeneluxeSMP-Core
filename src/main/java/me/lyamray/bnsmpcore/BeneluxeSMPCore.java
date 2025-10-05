package me.lyamray.bnsmpcore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.database.Database;
import me.lyamray.bnsmpcore.utils.manager.RegisterListenerManager;
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
        RegisterListenerManager.registerAll();
        Database.getInstance().setupDatabase();
        Database.getInstance().loadAllData();
    }

    @Override
    public void onDisable() {
        Database.getInstance().saveAllData();
        Database.getInstance().shutdown();
    }
}
