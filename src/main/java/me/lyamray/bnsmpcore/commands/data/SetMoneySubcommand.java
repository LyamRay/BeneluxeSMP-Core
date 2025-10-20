package me.lyamray.bnsmpcore.commands.data;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SetMoneySubcommand() {

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("setmoney")
                .then(playerArgument()
                        .then(amountArgument()));
    }

    private RequiredArgumentBuilder<CommandSourceStack, PlayerProfileListResolver> playerArgument() {
        return Commands.argument("player", ArgumentTypes.playerProfiles())
                .suggests((ctx, builder) -> {
                    Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(builder.getRemainingLowerCase()))
                            .forEach(builder::suggest);
                    return builder.buildFuture();
                });
    }

    private RequiredArgumentBuilder<CommandSourceStack, Integer> amountArgument() {
        return Commands.argument("amount", IntegerArgumentType.integer())
                .executes(ctx -> {
                    CommandSourceStack source = ctx.getSource();
                    var profiles = ctx.getArgument("player", PlayerProfileListResolver.class).resolve(source);
                    int amount = ctx.getArgument("amount", Integer.class);

                    if (profiles.isEmpty()) {
                        source.getSender().sendRichMessage("<red>No players found.");
                        return 0;
                    }

                    for (var profile : profiles) {
                        if (profile.getId() == null) continue;
                        PlayerData data = PlayerDataHandler.getInstance().getData(profile.getId());
                        data.setMoney(amount);
                        PlayerDataHandler.getInstance().setData(data);

                        Player online = Bukkit.getPlayer(profile.getId());
                        if (online != null) {
                            online.sendMessage(MiniMessage.miniMessage().deserialize(
                                    "<green>Your balance has been set to <amount>",
                                    Placeholder.unparsed("amount", String.valueOf(amount))
                            ));
                        }
                    }

                    source.getSender().sendRichMessage(
                            "<green>Set money for <count> player(s) to <amount>",
                            Placeholder.unparsed("count", String.valueOf(profiles.size())),
                            Placeholder.unparsed("amount", String.valueOf(amount))
                    );

                    return 1;
                });
    }
}
