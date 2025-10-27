package me.lyamray.beneluxesmpcore.utils.messages;

import lombok.Getter;
import me.lyamray.beneluxesmpcore.utils.ranks.Ranks;
import org.bukkit.entity.Player;

@Getter
public enum ChatMessages {

    PLAYER_HASNT_GOT_DATA((rank, player, str1, str2) -> """
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><bold>BeneluxeSMP</bold></gradient>
            <gradient:#D2E3E6:#C4D0CD>Hey, {playername}! Sorry voor het ongemak.</gradient>
            <gradient:#C6E5F1:#B0D0C0>
            Het lijkt alsof jouw data niet bestaat! Gelieve opnieuw te verbinden met de server
            of neem contact op via de discord. discord.gg/beneluxesmp</gradient>
            """.replace("{playername}", player.getName())),

    ADMIN_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    MODERATOR_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    HELPER_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    OVERLEVER_PLUS_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    AVONTURIER_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    VETERAAN_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    VERKENNER_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player))),
    OVERLEVER_RANK_CHATMESSAGE((rank, player, str1, str2) ->
            formatRankMessage(rank, player, str1, str2, getPlayerGradient(player)));

    private final TriFunction<Ranks, Player, String, String, String> messageFunction;

    ChatMessages(TriFunction<Ranks, Player, String, String, String> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public String getMessage(Ranks rank, Player player, String str1, String str2) {
        return messageFunction.apply(rank, player, str1, str2);
    }

    private static String formatRankMessage(Ranks rank, Player player, String str1, String str2, String messageGradient) {
        if (rank == null) rank = Ranks.OVERLEVER;
        String rankGradient = rank.getMessage();

        String gradientCodes = rankGradient
                .replaceAll("<gradient:", "")
                .replaceAll(">.*</gradient>", "");

        if (messageGradient == null || messageGradient.isEmpty()) {
            messageGradient = "#C3CBD2:#BAC7D6";
        }

        return String.format(
                "<gradient:%s>%s</gradient><gray> | </gray><gradient:%s>%s</gradient><gray> » </gray><gradient:%s>%s</gradient><gradient:%s>%s</gradient>",
                gradientCodes, rank.getDisplayName(),
                gradientCodes, player.getName(),
                messageGradient, str1 != null ? str1 : "",
                messageGradient, str2 != null ? str2 : ""
        );
    }

    private static String getPlayerGradient(Player player) {
        return "#C3CBD2:#BAC7D6";
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, D, R> {
        R apply(A a, B b, C c, D d);
    }
}
