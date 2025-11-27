package me.lyamray.beneluxesmpcore.commands.data.claimblocks;

import com.destroystokyo.paper.profile.PlayerProfile;
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
public record GetClaimBlocksSubcommand() {

    private static final Set<String> ALLOWED_RANKS = Set.of("ADMIN", "MODERATOR");

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("getclaimblocks")
                .then(playerProfilesArgument()
                        .executes(this::executeGetClaimBlocks));
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

    private int executeGetClaimBlocks(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
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

        for (PlayerProfile profile : profiles) {
            if (profile.getId() == null || profile.getName() == null) continue;

            UUID uuid = profile.getId();
            PlayerData data = PlayerDataHandler.getInstance().getData(uuid);

            long claimBlocks = data.getClaimBlocks();
            sendExecutorFeedback(source, profile.getName(), claimBlocks);
        }

        return 1;
    }

    private void sendExecutorFeedback(CommandSourceStack source, String name, long amount) {
        String message = (GlobalMessages.BENELUXE_TITLE.getMessage() +
                "<gray> » <gradient:#C6E5F1:#C4D0CD>Het aantal claimblocks van {playername} is momenteel {amount}.</gradient>")
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
