package lyamray.beneluxesmpapi.data;

import org.bukkit.entity.Player;

public interface PlayerDataAPI {
    int getCredits(Player player);
    void setCredits(Player player, int amount);
}
