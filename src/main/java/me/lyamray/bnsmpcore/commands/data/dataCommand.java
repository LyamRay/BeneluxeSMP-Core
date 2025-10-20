package me.lyamray.bnsmpcore.commands.data;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.Commands;
import lombok.Getter;

public class dataCommand {

    @Getter
    private static final dataCommand instance = new dataCommand();
    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("data")
                .then(new SetRankSubcommand().create())
                .build();
    }
}
