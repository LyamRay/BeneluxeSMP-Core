package me.lyamray.bnsmpcore.commands.data;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.PlayerProfileListResolver;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@NullMarked
public record GetRankSubcommand() {

    private static final Set<String> ALLOWED_RANKS = Set.of("ADMIN", "MODERATOR");

    public LiteralArgumentBuilder<CommandSourceStack> create() {
        return Commands.literal("getrank")
                .then(playerArgument()
                        .executes(ctx -> executeGetRank(
                                ctx.getSource(),
                                ctx.getArgument("player", PlayerProfileListResolver.class).resolve(ctx.getSource())
                        )));
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

    private int executeGetRank(CommandSourceStack source, Collection<PlayerProfile> profiles) {
        if (!hasPermission(source)) {
            source.getSender().sendRichMessage("<red>You do not have permission to execute this command.");
            return 0;
        }

        if (profiles.isEmpty()) {
            source.getSender().sendRichMessage("<red>No players found.");
            return 0;
        }

        for (PlayerProfile profile : profiles) {
            UUID uuid = profile.getId();
            if (uuid == null) continue;

            PlayerData data = PlayerDataHandler.getInstance().getData(uuid);

            source.getSender().sendRichMessage(
                    "<green>" + profile.getName() + "'s rank is " + data.getRank()
            );
        }

        return 1;
    }

    private boolean hasPermission(CommandSourceStack source) {
        if (source.getExecutor() == null) return true; // console can run

        UUID executorUuid = source.getExecutor().getUniqueId();
        PlayerData data = PlayerDataHandler.getInstance().getData(executorUuid);
        return ALLOWED_RANKS.stream().anyMatch(r -> r.equalsIgnoreCase(data.getRank()));
    }
}
