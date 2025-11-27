package me.lyamray.beneluxesmpcore.handlers.money;

import lombok.Getter;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import me.lyamray.beneluxesmpcore.utils.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyHandler {

    @Getter
    private static final MoneyHandler instance = new MoneyHandler();

    public long getBalance(UUID uuid) {
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        return data.getMoney();
    }

    public long getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public void deposit(UUID uuid, long amount) {
        if (amount <= 0) return;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setMoney(data.getMoney() + amount);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        notifyPlayer(player.getPlayer(), amount, true);
    }

    public void deposit(Player player, long amount) {
        deposit(player.getUniqueId(), amount);
    }

    public boolean withdraw(UUID uuid, long amount) {
        if (amount <= 0) return false;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        if (data.getMoney() < amount) return false;
        data.setMoney(data.getMoney() - amount);
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        notifyPlayer(player, amount, false);
        return true;
    }

    public boolean withdraw(Player player, long amount) {
        return withdraw(player.getUniqueId(), amount);
    }

    public void setBalance(UUID uuid, long amount) {
        if (amount < 0) amount = 0;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setMoney(amount);
        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        if (player == null) return;
        notifyPlayer(player, amount, false);
    }

    public void setBalance(Player player, long amount) {
        setBalance(player.getUniqueId(), amount);
    }

    public void resetBalance(UUID uuid) {
        setBalance(uuid, 0);
    }
    public void resetBalance(Player player) {
        resetBalance(player.getUniqueId());
    }

    public void notifyPlayer(Player player, long amount, boolean state) {
        player.playSound(player.getLocation(), Sound.UI_HUD_BUBBLE_POP, 1, 1);
        String label = state ? "+" : "-";
        String message = "<white> " + label + " <gray><gradient:#76ABC4:#A1CDDB>€" + NumberFormat.formatNumber(amount) + "</gradient></gray>";
        Component component = MiniMessage.deserializeMessage(message);
        player.sendActionBar(component);
    }
}
