package me.lyamray.beneluxesmpcore.api.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * API-accessible DataCommand.
 * <p>
 * Other plugins can register subcommands to the /data root command via {@link #registerSubCommand(Supplier)}.
 */
public class DataCommandAPI {

    private static final DataCommandAPI INSTANCE = new DataCommandAPI();

    public static DataCommandAPI getInstance() {
        return INSTANCE;
    }

    private final List<Supplier<LiteralArgumentBuilder<CommandSourceStack>>> subcommands = new ArrayList<>();

    private DataCommandAPI() {
    }

    /**
     * Registers a new subcommand to the /data command.
     * <p>
     * Other plugins can call this to add their own subcommands.
     *
     * @param subCommand a Supplier returning a {@link LiteralArgumentBuilder<CommandSourceStack>}
     */
    public void registerSubCommand(Supplier<LiteralArgumentBuilder<CommandSourceStack>> subCommand) {
        subcommands.add(subCommand);
    }

    /**
     * Builds the root /data command with all registered subcommands.
     *
     * @return the Brigadier command node for /data
     */
    public LiteralCommandNode<CommandSourceStack> buildCommand() {
        var root = Commands.literal("data");
        subcommands.forEach(builder -> root.then(builder.get()));
        return root.build();
    }
}
