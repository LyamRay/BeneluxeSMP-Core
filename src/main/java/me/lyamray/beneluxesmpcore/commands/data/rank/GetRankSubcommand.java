package me.lyamray.beneluxesmpcore.commands.data.rank;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
            sendNoPermissionMessage(source);
            return 0;
        }

        if (profiles.isEmpty()) {
            sendNoPlayersFoundMessage(source);
            return 0;
        }

        for (PlayerProfile profile : profiles) {
            UUID uuid = profile.getId();
            if (uuid == null) return 0;
            if (profile.getName() == null) return 0;

            PlayerData data = PlayerDataHandler.getInstance().getData(uuid);

            sendRankInfoMessage(source, profile.getName(), data.getRank());
        }

        return 1;
    }

    private boolean hasPermission(CommandSourceStack source) {
        if (source.getExecutor() == null) return true;

        UUID executorUuid = source.getExecutor().getUniqueId();
        PlayerData data = PlayerDataHandler.getInstance().getData(executorUuid);
        return ALLOWED_RANKS.stream().anyMatch(r -> r.equalsIgnoreCase(data.getRank()));
    }

    private void sendNoPermissionMessage(CommandSourceStack source) {
        source.getSender().sendMessage(MiniMessage.deserializeMessage(
                GlobalMessages.BENELUXE_TITLE.getMessage() +
                        "<gray> » <red>Je hebt niet de juiste permissies om dit commando uit te voeren.</red>"
        ));
    }

    private void sendNoPlayersFoundMessage(CommandSourceStack source) {
        source.getSender().sendMessage(MiniMessage.deserializeMessage(
                GlobalMessages.BENELUXE_TITLE.getMessage() +
                        "<gray> » <red>Geen spelers gevonden.</red>"
        ));
    }

    private void sendRankInfoMessage(CommandSourceStack source, String playerName, String rank) {
        String message = GlobalMessages.BENELUXE_TITLE.getMessage() +
                "<gray> » <gradient:#C6E5F1:#C4D0CD>De rank van {playername} is: <#D2E3E6>{rank}</#D2E3E6>.</gradient>"
                        .replace("{playername}", playerName)
                        .replace("{rank}", rank);

        source.getSender().sendMessage(MiniMessage.deserializeMessage(message));
    }
}
