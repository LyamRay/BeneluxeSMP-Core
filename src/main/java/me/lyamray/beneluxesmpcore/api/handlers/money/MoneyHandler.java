package me.lyamray.beneluxesmpcore.api.handlers.money;

import lombok.Getter;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyHandler {

    @Getter
    private static final MoneyHandler instance = new MoneyHandler();

    private MoneyHandler() {}

    /**
     * Get the balance of a player.
     * @param uuid Player UUID
     * @return Current balance
     */
    public long getBalance(UUID uuid) {
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        return data.getMoney();
    }

    public long getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    /**
     * Deposit money into a player's account.
     * @param uuid Player UUID
     * @param amount Amount to deposit (must be positive)
     */
    public void deposit(UUID uuid, long amount) {
        if (amount <= 0) return;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setMoney(data.getMoney() + amount);
    }

    public void deposit(Player player, long amount) {
        deposit(player.getUniqueId(), amount);
    }

    /**
     * Withdraw money from a player's account.
     * @param uuid Player UUID
     * @param amount Amount to withdraw (must be positive)
     * @return True if successful, false if insufficient balance
     */
    public boolean withdraw(UUID uuid, long amount) {
        if (amount <= 0) return false;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        if (data.getMoney() < amount) return false;
        data.setMoney(data.getMoney() - amount);
        return true;
    }

    public boolean withdraw(Player player, long amount) {
        return withdraw(player.getUniqueId(), amount);
    }

    /**
     * Set a player's balance directly.
     * @param uuid Player UUID
     * @param amount New balance (non-negative)
     */
    public void setBalance(UUID uuid, long amount) {
        if (amount < 0) amount = 0;
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setMoney(amount);
    }

    public void setBalance(Player player, long amount) {
        setBalance(player.getUniqueId(), amount);
    }
}
