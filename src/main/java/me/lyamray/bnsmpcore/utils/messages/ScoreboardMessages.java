package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public enum ScoreboardMessages {

    DEFAULT_SCOREBOARD((player, claimBlocks, money, rank) -> List.of(
            MiniMessage.deserializeMessage(" "),
            MiniMessage.deserializeMessage(
                    "<gradient:#B4CBD0:#A4BCC3>Rank</gradient><gray> » </gray>" + rank
            ),
            MiniMessage.deserializeMessage(
                    "<gradient:#B4CBD0:#A4BCC3>Claimblocks</gradient><gray> » </gray><gradient:#AFD0DD:#7DBAD0>" + claimBlocks + "</gradient>"
            ),
            MiniMessage.deserializeMessage(
                    "<gradient:#B4CBD0:#A4BCC3>Money</gradient><gray> » </gray><gradient:#AFD0DD:#7DBAD0>€" + money + "</gradient>"
            ),
            MiniMessage.deserializeMessage(" "), // Spacer
            MiniMessage.deserializeMessage("<gradient:#D2E3E6:#D2E3E6>play.</gradient><gradient:#C6E5F1:#C4D0CD>beneluxesmp.be</gradient>")
    ));

    private final MultiFunction<Player, Integer, Integer, String, List<Component>> messageFunction;

    ScoreboardMessages(MultiFunction<Player, Integer, Integer, String, List<Component>> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public List<Component> getLines(Player player, Integer claimBlocks, Integer money, String rank) {
        return messageFunction.apply(player, claimBlocks, money, rank);
    }

    @FunctionalInterface
    public interface MultiFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
