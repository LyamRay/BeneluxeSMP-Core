package me.lyamray.beneluxesmpcore.commands.data;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;
import me.lyamray.beneluxesmpcore.commands.data.claimblocks.GetClaimBlocksSubcommand;
import me.lyamray.beneluxesmpcore.commands.data.claimblocks.SetClaimBlocksSubcommand;
import me.lyamray.beneluxesmpcore.commands.data.credits.GetCreditsSubcommand;
import me.lyamray.beneluxesmpcore.commands.data.credits.SetCreditsSubcommand;
import me.lyamray.beneluxesmpcore.commands.data.money.GetMoneySubcommand;
import me.lyamray.beneluxesmpcore.commands.data.money.SetMoneySubcommand;
import me.lyamray.beneluxesmpcore.commands.data.rank.GetRankSubcommand;
import me.lyamray.beneluxesmpcore.commands.data.rank.SetRankSubcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataCommand {

    @Getter
    private static final DataCommand instance = new DataCommand();

    private final List<Supplier<LiteralArgumentBuilder<CommandSourceStack>>> subcommands = new ArrayList<>();

    private DataCommand() {
        registerSubCommand(() -> new SetRankSubcommand().create());
        registerSubCommand(() -> new GetRankSubcommand().create());
        registerSubCommand(() -> new SetMoneySubcommand().create());
        registerSubCommand(() -> new GetMoneySubcommand().create());
        registerSubCommand(() -> new SetClaimBlocksSubcommand().create());
        registerSubCommand(() -> new GetClaimBlocksSubcommand().create());
        registerSubCommand(() -> new SetCreditsSubcommand().create());
        registerSubCommand(() -> new GetCreditsSubcommand().create());
    }

    public void registerSubCommand(Supplier<LiteralArgumentBuilder<CommandSourceStack>> subCommand) {
        subcommands.add(subCommand);
    }

    public LiteralCommandNode<CommandSourceStack> create() {
        var root = Commands.literal("bdata");

        subcommands.forEach(builder -> root.then(builder.get()));

        return root.build();
    }
}
