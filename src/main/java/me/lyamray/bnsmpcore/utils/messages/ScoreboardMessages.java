package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public enum ScoreboardMessages {

    DEFAULT_SCOREBOARD((player, claimBlocks, money) -> List.of(
            MiniMessage.deserializeMessage("\n"),
            MiniMessage.deserializeMessage(
                    "<gradient:#B4CBD0:#A4BCC3>Claimblocks</gradient><gray> » </gray><gradient:#AFD0DD:#619AB1>" + claimBlocks + "</gradient>"
            ),
            MiniMessage.deserializeMessage(
                    "<gradient:#B4CBD0:#A4BCC3>Money</gradient><gray> » </gray><gradient:#AFD0DD:#619AB1>€"+ money +"</gradient>"
            ),
            MiniMessage.deserializeMessage("\n"),
            MiniMessage.deserializeMessage("<gray>play.beneluxesmp.be</gray>")
    ));

    private final MultiFunction<Player, Integer, Integer, List<Component>> messageFunction;

    ScoreboardMessages(MultiFunction<Player, Integer, Integer, List<Component>> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public List<Component> getLines(Player player, Integer claimBlocks, Integer money) {
        return messageFunction.apply(player, claimBlocks, money);
    }

    @FunctionalInterface
    public interface MultiFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}
