package me.lyamray.bnsmpcore.commands.data;

import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class dataCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("bnsmp.setrank")) {
            sender.sendMessage("§cYou don’t have permission to use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§eUsage: /setrank <player> <rank>");
            return true;
        }

        String targetName = args[0];
        String rankName = args[1].toUpperCase();

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        Ranks rank;
        try {
            rank = Ranks.valueOf(rankName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid rank. Available ranks:");
            for (Ranks r : Ranks.values()) {
                sender.sendMessage(" §7- " + r.name());
            }
            return true;
        }

        PlayerData data = PlayerDataHandler.getInstance().getData(target.getUniqueId());
        data.setRank(rank.name());
        PlayerDataHandler.getInstance().setData(data);

        sender.sendMessage("§aSet " + target.getName() + "'s rank to " + rank.name());
        target.sendMessage("§aYour rank has been updated to " + rank.name());

        return true;
    }
}
