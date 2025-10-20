package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.function.Function;

@Getter
public enum PlayerMessages {

    PLAYER_FIRST_TIME_JOIN_MESSAGE(player -> ("""
            
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient><gray> » \
            <gradient:#D2E3E6:#D2E3E6>Hey, {playername}! Welkom op de BeneluxeSMP</gradient><gradient:#C6E5F1:#C4D0CD> \
            server, heel veel speelplezier!</gradient>
            """)
            .replace("{playername}", player.getName())),

    PLAYER_JOIN_MESSAGE(player -> ("""
            
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient><gray> » \
            <gradient:#D2E3E6:#D2E3E6>Hey, {playername}! Welkom terug op de BeneluxeSMP</gradient><gradient:#C6E5F1:#C4D0CD> \
            server, heel veel speelplezier!</gradient>
            """)
            .replace("{playername}", player.getName())),

    TITLE_HAS_NOT_JOINED(player -> ("""
            <gradient:#BFE7EA:#A4D0E1:#BFE7EA>Welkom!</gradient>""")),

    TITLE_HAS_JOINED(player -> ("""
            <gradient:#BFE7EA:#A4D0E1:#BFE7EA>Welkom terug!</gradient>""")),

    SUBTITLE(player -> ("""
            <gradient:#D2E3E6:#D2E3E6>Veel </gradient><gradient:#C6E5F1:#C4D0CD>speelplezier!</gradient>""")),

    PLAYER_MUST_NOT_WALK_HERE(player -> ("""
            
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient><gray> » \
            <gradient:#D2E3E6:#D2E3E6>Hey, {playername}! Helaas mag jij hier niet komen,</gradient><gradient:#C6E5F1:#C4D0CD> \
            je wordt terug naar de spawn getped!</gradient>
            """)
            .replace("{playername}", player.getName()));

    private final Function<Player, String> messageFunction;

    PlayerMessages(Function<Player, String> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public String getMessage(Player player) {
        return messageFunction.apply(player);
    }
}