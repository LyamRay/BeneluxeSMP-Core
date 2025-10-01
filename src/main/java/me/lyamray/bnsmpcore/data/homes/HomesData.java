package me.lyamray.bnsmpcore.data.homes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomesData {

    private UUID playerUuid;
    private String homeName;
    private int x, y, z;
    private String world;

}