package me.lyamray.bnsmpcore.utils.ranks;

import lombok.Getter;

@Getter
public enum Ranks {

    OVERLEVER("<gradient:#BBCEDD:#BBC8DB>Overlever</gradient>"),
    VERKENNER("<gradient:#E2D8EA:#E0CCEC>Verkenner</gradient>"),
    AVONTURIER("<gradient:#C6E3D0:#9ED2B0>Avonturier</gradient>"),
    OVERLEVER_PLUS("<gradient:#A1A2D6:#5261D9>Overlever+</gradient>"),
    VETERAAN("<gradient:#E5B0CA:#D66C9F>Veteraan</gradient>"),
    HELPER("<gradient:#E0DDB3:#A2A07F>Helper</gradient>"),
    MODERATOR("<gradient:#4F64C1:#333F72>Moderator</gradient>"),
    ADMIN("<gradient:#DD5F70:#D7243A>Admin</gradient>");

    private final String rankGradients;

    Ranks(String rankGradients) {
        this.rankGradients = rankGradients;
    }

    public String getMessage() {
        return rankGradients;
    }
}