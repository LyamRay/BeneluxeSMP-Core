package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public enum ChatMessages {

    PLAYER_HASNT_GOT_DATA((player, string1, string2) -> """
            <gray>• </gray>
            <gradient:#BFE7EA:#A4D0E1:#BFE7EA><bold>BeneluxeSMP</bold></gradient>
            <gray>» </gray>
            <gradient:#D2E3E6:#C4D0CD>Hey, {playername}! Sorry voor het ongemak.</gradient>
            <gradient:#C6E5F1:#B0D0C0>
            Het lijkt alsof jouw data niet bestaat! Gelieve opnieuw te verbinden met de server
            of neem contact op via de <a href="https://discord.gg/beneluxesmp">Discord</a>.
            </gradient>
            """.replace("{playername}", player.getName())),

    OVERLEVER_RANK_CHATMESSAGE((player, string1, string2) -> """
        <gradient:#BBCEDD:#BBC8DB>[Overlever]</gradient><gradient:#BBC8DB:#BBCEDD> {playername}</gradient>\
        <gray> » </gray><gradient:#C3CBD2:#BAC7D6>{string1}</gradient><gradient:#BAC7D6:#C3CBD2>{string2}</gradient>
        """
            .replace("{playername}", player.getName())
            .replace("{string1}", string1 != null ? string1 : "")
            .replace("{string2}", string2 != null ? string2 : "")),

    AVONTURIER_RANK_CHATMESSAGE((player, string1, string2) -> """
            <gradient:#BBCEDD:#BBC8DB>[Avonturier]</gradient><gradient:#BBC8DB:#BBCEDD> {playername}</gradient>\
            <gray> » </gray><gradient:#C3CBD2:#BAC7D6>{string1}</gradient><gradient:#BAC7D6:#C3CBD2>{string2}</gradient>
            """
            .replace("{playername}", player.getName())
            .replace("{string1}", string1 != null ? string1 : "")
            .replace("{string2}", string2 != null ? string2 : ""));

    private final TriFunction<Player, String, String, String> messageFunction;

    ChatMessages(TriFunction<Player, String, String, String> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public String getMessage(Player player, String str1, String str2) {
        return messageFunction.apply(player, str1, str2);
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }
}