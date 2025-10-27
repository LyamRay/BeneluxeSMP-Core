package me.lyamray.beneluxesmpcore.utils.messages;

import lombok.Getter;
import me.lyamray.beneluxesmpcore.utils.ranks.Ranks;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
public enum TabMessages {

    HEADER(aantal -> """
            
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient>
            
            <gradient:#D2E3E6:#D2E3E6>Spelers online: {aantal}</gradient>
            """.replace("{aantal}", aantal.toString())),

    FOOTER(aantal -> """
            
            <gradient:#D2E3E6:#D2E3E6>play.</gradient><gradient:#C6E5F1:#C4D0CD>beneluxesmp.be!</gradient>
            """),

    PLAYER_ENTRY((rank, player) -> {
        if (rank == null) rank = Ranks.OVERLEVER;
        String rankGradient = rank.getMessage();

        String gradientCodes = rankGradient
                .replaceAll("<gradient:", "")
                .replaceAll(">.*</gradient>", "");

        return String.format(
                "<gradient:%s>%s</gradient><gray> | </gray><gradient:%s>%s</gradient><gray>",
                gradientCodes, rank.getDisplayName(),
                gradientCodes, player.getName()
        );
    });

    private final Function<Integer, String> messageFunction;
    private final BiFunction<Ranks, Player, String> playerFunction;

    TabMessages(Function<Integer, String> messageFunction) {
        this.messageFunction = messageFunction;
        this.playerFunction = null;
    }

    TabMessages(BiFunction<Ranks, Player, String> playerFunction) {
        this.playerFunction = playerFunction;
        this.messageFunction = null;
    }

    public String getMessage(int aantal) {
        return messageFunction != null ? messageFunction.apply(aantal) : "";
    }

    public String getPlayerMessage(Ranks rank, Player player) {
        return playerFunction != null ? playerFunction.apply(rank, player) : "";
    }
}
