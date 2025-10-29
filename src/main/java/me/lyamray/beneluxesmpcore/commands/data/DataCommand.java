package me.lyamray.beneluxesmpcore.commands.data;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;

public class DataCommand {

    @Getter
    private static final DataCommand instance = new DataCommand();
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("data")
                .then(new SetRankSubcommand().create())
                .then(new GetRankSubcommand().create())
                .then(new SetMoneySubcommand().create())
                .then(new GetMoneySubcommand().create())
                .then(new SetClaimBlocksSubcommand().create())
                .then(new GetClaimBlocksSubcommand().create())
                .then(new SetCreditsSubcommand().create())
                .then(new GetCreditsSubcommand().create())
                .build();
    }
}
