package me.lyamray.bnsmpcore.commands.data;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

import static java.util.Arrays.stream;

@NullMarked
public record SetRankSubcommand() {

    private static final Set<String> ALLOWED_RANKS = Set.of("ADMIN", "MODERATOR", "HELPER");

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
                .suggests((ctx, builder) -> {
                    stream(Ranks.values())
                            .map(Enum::name)
                            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                })
                .executes(ctx -> {
                    final CommandSourceStack source = ctx.getSource();

                    if (!hasRequiredRank(source)) {
                        source.getSender().sendRichMessage("<red>You do not have permission to execute this command.");
                        return 0;
                    }

                    var profilesResolver = ctx.getArgument("player", PlayerProfileListResolver.class);
                    var profiles = profilesResolver.resolve(source);

                    if (profiles.isEmpty()) {
                        source.getSender().sendRichMessage("<red>No players found.");
                        return 0;
                    }

                    Ranks rank = resolveRank(ctx, source);

                    for (var profile : profiles) {
                        PlayerData data = PlayerDataHandler.getInstance().getData(profile.getId());
                        data.setRank(rank.name());
                        PlayerDataHandler.getInstance().setData(data);

                        if (profile.getId() == null) return 0;

                        Player onlinePlayer = Bukkit.getPlayer(profile.getId());
                        if (onlinePlayer != null) {
                            onlinePlayer.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<green>Your rank has been updated to <rank>",
                                    Placeholder.unparsed("rank", rank.name())
                            ));
                        }
                    }

                    // Send feedback to executor
                    source.getSender().sendRichMessage(
                            "<green>Updated rank for <count> player(s) to <rank>",
                            Placeholder.unparsed("count", String.valueOf(profiles.size())),
                            Placeholder.unparsed("rank", rank.name())
                    );

                    return 1;
                });
    }

    private boolean hasRequiredRank(CommandSourceStack source) {
        var executor = source.getExecutor();
        if (executor == null) return true; // console

        PlayerData playerData = PlayerDataHandler.getInstance().getData(executor.getUniqueId());
        return ALLOWED_RANKS.stream()
                .anyMatch(rank -> rank.equalsIgnoreCase(playerData.getRank()));
    }

    private Ranks resolveRank(CommandContext<CommandSourceStack> ctx, CommandSourceStack source) throws CommandSyntaxException {
        String rankName = ctx.getArgument("rank", String.class).toUpperCase();
        try {
            return Ranks.valueOf(rankName);
        } catch (IllegalArgumentException e) {
            source.getSender().sendRichMessage("<red>Invalid rank. Available ranks:");
            for (Ranks r : Ranks.values()) {
                source.getSender().sendRichMessage("<gray>- " + r.name());
            }
            throw new CommandSyntaxException(null,
                    (Message) MiniMessage.miniMessage().deserialize("<red>Invalid rank"));
        }
    }
}
