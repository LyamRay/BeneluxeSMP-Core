package me.lyamray.bnsmpcore.commands.data;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.GlobalMessages;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.stream;

@NullMarked
public record SetRankSubcommand() {

    private static final Set<String> ALLOWED_RANKS = Set.of("ADMIN");

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("setrank")
                .then(playerProfilesArgument()
                        .then(rankArgument()));
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

    private RequiredArgumentBuilder<CommandSourceStack, String> rankArgument() {
        return Commands.argument("rank", StringArgumentType.word())
                .suggests(this::suggestRanks)
                .executes(this::executeSetRank);
    }

    private CompletableFuture<Suggestions> suggestRanks(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        stream(Ranks.values())
                .map(Enum::name)
                .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                .forEach(builder::suggest);
        return builder.buildFuture();
    }

    private int executeSetRank(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();

        if (!hasRequiredRank(source)) {
            sendNoPermissionMessage(source);
            return 0;
        }

        var profiles = resolveProfiles(ctx, source);
        if (profiles.isEmpty()) return 0;

        Optional<Ranks> optionalRank = resolveRank(ctx, source);
        if (optionalRank.isEmpty()) return 0;

        Ranks rank = optionalRank.get();

        for (var profile : profiles) {
            if (profile.getId() == null) return 0;

            updatePlayerRank(profile.getId(), rank);
            sendRankUpdatedMessage(profile.getId(), rank);
            sendExecutorFeedback(source, profile.getName(), rank);
        }

        return 1;
    }

    private Collection<PlayerProfile> resolveProfiles(CommandContext<CommandSourceStack> ctx, CommandSourceStack source) throws CommandSyntaxException {
        var resolver = ctx.getArgument("player", PlayerProfileListResolver.class);
        var profiles = resolver.resolve(source);

        if (profiles.isEmpty()) {
            source.getSender().sendMessage(MiniMessage.deserializeMessage(
                    GlobalMessages.BENELUXE_TITLE.getMessage() +
                            "<gray> » <red>Geen spelers gevonden.</red>"
            ));
        }
        return profiles;
    }

    private void updatePlayerRank(UUID uuid, Ranks rank) {
        PlayerData data = PlayerDataHandler.getInstance().getData(uuid);
        data.setRank(rank.name());
        PlayerDataHandler.getInstance().setData(data);
    }

    private void sendRankUpdatedMessage(UUID uuid, Ranks rank) {
        Player onlinePlayer = Bukkit.getPlayer(uuid);
        if (onlinePlayer != null) {
            String message = (GlobalMessages.BENELUXE_TITLE.getMessage() +
                    "<gray> » <gradient:#D2E3E6:#D2E3E6>Hey, {playername}! Je hebt een nieuwe rank ontvangen. </gradient><gradient:#C6E5F1:#C4D0CD>" +
                    "Jij hebt nu de rank: {rank}!</gradient>")
                    .replace("{playername}", onlinePlayer.getName())
                    .replace("{rank}", rank.name());

            onlinePlayer.sendMessage(MiniMessage.deserializeMessage(message));
        }
    }

    private void sendExecutorFeedback(CommandSourceStack source, String name, Ranks rank) {
        String message = (GlobalMessages.BENELUXE_TITLE.getMessage() +
                "<gray> » <gradient:#C6E5F1:#C4D0CD>Je hebt succesvol de rank van {playername} aangepast naar {rank}.</gradient>")
                .replace("{playername}", name)
                .replace("{rank}", rank.name());

        source.getSender().sendMessage(MiniMessage.deserializeMessage(message));
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

    private Optional<Ranks> resolveRank(CommandContext<CommandSourceStack> ctx, CommandSourceStack source) {
        String rankName = ctx.getArgument("rank", String.class).toUpperCase();
        try {
            return Optional.of(Ranks.valueOf(rankName));
        } catch (IllegalArgumentException e) {
            source.getSender().sendMessage(MiniMessage.deserializeMessage(
                    GlobalMessages.BENELUXE_TITLE.getMessage() +
                            "<gray> » <red>Je hebt een ongeldige rank opgegeven. Beschikbare ranks:</red>"
            ));
            for (Ranks r : Ranks.values()) {
                source.getSender().sendMessage(MiniMessage.deserializeMessage(
                        "<gray>- <#C6E5F1>" + r.name() + "</#C6E5F1>"
                ));
            }
            return Optional.empty();
        }
    }
}
