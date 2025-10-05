package me.lyamray.bnsmpcore.data.warps;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarpsData {

    private String name;
    private int x, y, z;
    private String world;
    private String requiredRank;

}
