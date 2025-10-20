package me.lyamray.bnsmpcore.commands.data;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.ranks.Ranks;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;


@NullMarked
public record SetRankSubcommand() {

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("setrank")
                .then(Commands.argument("player", ArgumentTypes.player())
                        .suggests((ctx, builder) -> {
                            Bukkit.getOnlinePlayers().stream()
                                    .map(Player::getName)
                                    .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                    .forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("rank", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    java.util.Arrays.stream(Ranks.values())
                                            .map(Enum::name)
                                            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                                            .forEach(builder::suggest);
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    final CommandSourceStack source = ctx.getSource();

                                    // Resolve player
                                    final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                    final Player target = targetResolver.resolve(source).getFirst();

                                    if (target == null) {
                                        source.getSender().sendRichMessage("<red>Player not found.");
                                        return 0;
                                    }

                                    // Resolve rank
                                    final String rankName = ctx.getArgument("rank", String.class).toUpperCase();
                                    Ranks rank;
                                    try {
                                        rank = Ranks.valueOf(rankName);
                                    } catch (IllegalArgumentException e) {
                                        source.getSender().sendRichMessage("<red>Invalid rank. Available ranks:");
                                        for (Ranks r : Ranks.values()) {
                                            source.getSender().sendRichMessage("<gray>- " + r.name());
                                        }
                                        return 0;
                                    }

                                    // Update player data
                                    PlayerData data = PlayerDataHandler.getInstance().getData(target.getUniqueId());
                                    data.setRank(rank.name());
                                    PlayerDataHandler.getInstance().setData(data);

                                    // Send feedback messages
                                    source.getSender().sendRichMessage(
                                            "<green>Set <name>'s rank to <rank>",
                                                    Placeholder.unparsed("name", target.getName()),
                                                    Placeholder.unparsed("rank", rank.name())
                                    );

                                    target.sendMessage(
                                            MiniMessage.miniMessage().deserialize("<green>Your rank has been updated to <rank>",
                                                    Placeholder.unparsed("rank", rank.name())
                                            )
                                    );

                                    return 1;
                                })
                        )
                );
    }
}
