package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import me.lyamray.bnsmpcore.utils.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public enum ScoreboardMessages {

    DEFAULT_SCOREBOARD((player, claimBlocks, money, rank, credits) -> {

        ZonedDateTime timeZone = ZonedDateTime.now(ZoneId.of("Europe/Brussels"));
        String formattedDateTime = timeZone.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm"));
        String escapedDateTime = MiniMessage.escapeTags(formattedDateTime);

        return List.of(
                MiniMessage.deserializeMessage(" "),
                MiniMessage.deserializeMessage("<gradient:#CDDDDD:#B5C1C1>" + escapedDateTime + "</gradient>"),
                MiniMessage.deserializeMessage(" "),
                MiniMessage.deserializeMessage(
                        "<gradient:#B4CBD0:#A4BCC3>Rank</gradient><gray> » </gray>" + rank
                ),
                MiniMessage.deserializeMessage(
                        "<gradient:#B4CBD0:#A4BCC3>Claimblocks</gradient><gray> » </gray><gradient:#AFD0DD:#9EC6D4>" + claimBlocks + "</gradient>"
                ),
                MiniMessage.deserializeMessage(
                        "<gradient:#B4CBD0:#A4BCC3>Credits</gradient><gray> » </gray><gradient:#AFD0DD:#9EC6D4>" + credits + "</gradient>"
                ),
                MiniMessage.deserializeMessage(
                        "<gradient:#B4CBD0:#A4BCC3>Saldo</gradient><gray> » </gray><gradient:#AFD0DD:#9EC6D4>€" + NumberFormat.formatNumber(money) + "</gradient>"
                ),
                MiniMessage.deserializeMessage(" "),
                MiniMessage.deserializeMessage("<gradient:#D2E3E6:#D2E3E6>play.</gradient><gradient:#C6E5F1:#C4D0CD>beneluxesmp.be</gradient>")
        );
    });

    private final MultiFunction<Player, Long, Long, String, Integer, List<Component>> messageFunction;

    ScoreboardMessages(MultiFunction<Player, Long, Long, String, Integer, List<Component>> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public List<Component> getLines(Player player, Long claimBlocks, Long money, String rank, Integer credits) {
        return messageFunction.apply(player, claimBlocks, money, rank, credits);
    }

    @FunctionalInterface
    public interface MultiFunction<A, B, C, D, E, R> {
        R apply(A a, B b, C c, D d, E e);
    }
}
