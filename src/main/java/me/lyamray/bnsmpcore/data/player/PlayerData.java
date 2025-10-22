package me.lyamray.bnsmpcore.data.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerData {

    @Getter
    private static final PlayerData instance = new PlayerData();

    private UUID uuid;
    private String name;
    private long money;
    private long playtime;
    private String rank;
    private boolean scoreboardEnabled;
    private long claimBlocks;

}