package me.lyamray.bnsmpcore.utils.ranks;

import lombok.Getter;

@Getter
public enum Ranks {

    ADMIN("<gradient:#DD5F70:#D7243A>Admin</gradient>", 8, "Admin"),
    MODERATOR("<gradient:#4F64C1:#333F72>Moderator</gradient>", 7, "Moderator"),
    HELPER("<gradient:#E3E7C2:#DBDEAB>Helper</gradient>", 6, "Helper"),
    VETERAAN("<gradient:#E5B0CA:#D66C9F>Veteraan</gradient>", 5, "Veteraan"),
    OVERLEVER_PLUS("<gradient:#A1A2D6:#5261D9>Overlever+</gradient>", 4, "Overlever+"),
    AVONTURIER("<gradient:#C6E3D0:#9ED2B0>Avonturier</gradient>", 3, "Avonturier"),
    VERKENNER("<gradient:#E2D8EA:#E0CCEC>Verkenner</gradient>", 2, "Verkenner"),
    OVERLEVER("<gradient:#BBCEDD:#BBC8DB>Overlever</gradient>", 1, "Overlever");

    private final String rankGradients;
    private final int weight;
    private final String displayName;

    Ranks(String rankGradients, int weight, String displayName) {
        this.rankGradients = rankGradients;
        this.weight = weight;
        this.displayName = displayName;
    }

    public String getMessage() {
        return rankGradients;
    }
}
