package me.lyamray.beneluxesmpcore.commands.data;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.GlobalMessages;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Set;
import java.util.UUID;

@NullMarked
public record SetMoneySubcommand() {

    private static final Set<String> ALLOWED_RANKS = Set.of("ADMIN");

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("setmoney")
                .then(playerProfilesArgument()
                        .then(amountArgument()));
    }

    private RequiredArgumentBuilder<CommandSourceStack, PlayerProfileListResolver> playerProfilesArgument() {
        return Commands.argument("player", ArgumentTypes.playerProfiles())
                .suggests((ctx, builder) -> {
                    Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }

    private RequiredArgumentBuilder<CommandSourceStack, Long> amountArgument() {
        return Commands.argument("amount", LongArgumentType.longArg(0))
                .executes(this::executeSetMoney);
    }

    private int executeSetMoney(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();

        if (!hasRequiredRank(source)) {
            sendNoPermissionMessage(source);
            return 0;
        }

        var resolver = ctx.getArgument("player", PlayerProfileListResolver.class);
        var profiles = resolver.resolve(source);

        if (profiles.isEmpty()) {
            sendNoPlayersFoundMessage(source);
            return 0;
        }

        long amount = ctx.getArgument("amount", Long.class);

        for (PlayerProfile profile : profiles) {
            if (profile.getId() == null) return 0;
            if (profile.getName() == null) return 0;


            UUID uuid = profile.getId();
            updatePlayerMoney(uuid, amount);
            sendMoneyUpdatedMessage(uuid, amount);
            sendExecutorFeedback(source, profile.getName(), amount);
        }

        return 1;
    }


    private void updatePlayerMoney(UUID uuid, long amount) {
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setMoney(amount);
        PlayerDataHandler.getInstance().setData(data);
    }


    private void sendMoneyUpdatedMessage(UUID uuid, long amount) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer == null) return;

        String message = (GlobalMessages.BENELUXE_TITLE.getMessage() +
                "<gray> » <gradient:#D2E3E6:#D2E3E6>Hey, {playername}! </gradient>" +
                "<gradient:#C6E5F1:#C4D0CD>Je saldo is ingesteld op {amount} coins.</gradient>")
                .replace("{playername}", onlinePlayer.getName())
                .replace("{amount}", String.valueOf(amount));

        onlinePlayer.sendMessage(MiniMessage.deserializeMessage(message));
    }

    private void sendExecutorFeedback(CommandSourceStack source, String name , long amount) {
        String message = (GlobalMessages.BENELUXE_TITLE.getMessage() +
                "<gray> » <gradient:#C6E5F1:#C4D0CD>Je hebt succesvol het saldo van {playername} aangepast naar {amount} coins.</gradient>")
                .replace("{playername}", name)
                .replace("{amount}", String.valueOf(amount));

        source.getSender().sendMessage(MiniMessage.deserializeMessage(message));
    }

    private void sendNoPlayersFoundMessage(CommandSourceStack source) {
        source.getSender().sendMessage(MiniMessage.deserializeMessage(
                GlobalMessages.BENELUXE_TITLE.getMessage() +
                        "<gray> » <red>Geen spelers gevonden.</red>"
        ));
    }

    private void sendNoPermissionMessage(CommandSourceStack source) {
        source.getSender().sendMessage(MiniMessage.deserializeMessage(
                GlobalMessages.BENELUXE_TITLE.getMessage() +
                        "<gray> » <red>Je hebt niet de juiste permissies om dit commando uit te voeren.</red>"
        ));
    }

    private boolean hasRequiredRank(CommandSourceStack source) {
        var executor = source.getExecutor();
        if (executor == null) return true;

        PlayerData playerData = PlayerDataHandler.getInstance().getData(executor.getUniqueId());
        return ALLOWED_RANKS.stream()
                .anyMatch(rank -> rank.equalsIgnoreCase(playerData.getRank()));
    }
}
