package me.lyamray.beneluxesmpcore.handlers.holograms;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.beneluxesmpcore.BeneluxeSMPCore;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import me.lyamray.beneluxesmpcore.utils.numbers.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class RichestPlayersHologramHandler {

    @Getter
    private static final RichestPlayersHologramHandler instance = new RichestPlayersHologramHandler();

    private static final String HOLOGRAM_NAME = "rijkste_spelers";
    private static final String SPAWN_WORLD = "spawn";
    private static final int TOP_PLAYERS = 10;

    private BukkitRunnable updateTask;

    public void startUpdateTask(long intervalTicks) {
        stopUpdateTask();

        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateRichestPlayersHologram();
            }
        };

        updateTask.runTaskTimerAsynchronously(BeneluxeSMPCore.getInstance(), 20L, intervalTicks);
        log.info("Started hologram update task ({} ticks).", intervalTicks);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
            log.info("Stopped hologram update task.");
        }
    }

    public void updateRichestPlayersHologram() {
        World world = Bukkit.getWorld(SPAWN_WORLD);
        if (world == null) return;

        List<PlayerData> topPlayers = getTopPlayersByMoney();

        List<String> playerLines = IntStream.range(0, TOP_PLAYERS)
                .mapToObj(i -> {
                    PlayerData data = (i < topPlayers.size()) ? topPlayers.get(i) : null;
                    String name = Optional.ofNullable(data)
                            .map(PlayerData::getName)
                            .filter(n -> !n.equalsIgnoreCase("Unknown"))
                            .orElse("Onbekend");
                    String money = Optional.ofNullable(data)
                            .map(PlayerData::getMoney)
                            .map(NumberFormat::formatNumber)
                            .orElse("0");
                    return MiniMessage.serializeComponent(MiniMessage.deserializeMessage(
                            String.format(
                                    "<gradient:#B4CBD0:#A4BCC3>%d • %s</gradient><gray> » </gray><gradient:#AFD0DD:#9EC6D4>%s</gradient>",
                                    i + 1, name, money
                            )
                    ));
                })
                .collect(Collectors.toList());

        playerLines.addFirst(MiniMessage.serializeComponent(MiniMessage.deserializeMessage(
                "<gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>Top 10 Rijkste Spelers</b>:</gradient><gray>"
        )));

        Location hologramLocation = new Location(world, -1, 100, -7).toCenterLocation();

        if (HologramHandler.getInstance().getHologram(HOLOGRAM_NAME) == null) {
            HologramHandler.getInstance().createTextHologram(HOLOGRAM_NAME, hologramLocation,
                    playerLines.toArray(new String[0]));
        } else {
            HologramHandler.getInstance().updateTextHologram(HOLOGRAM_NAME,
                    playerLines.toArray(new String[0]));
            HologramHandler.getInstance().updateHologramLocation(HOLOGRAM_NAME, hologramLocation);
        }
    }

    public void removeHologram() {
        HologramHandler.getInstance().removeHologram(HOLOGRAM_NAME);
    }

    private List<PlayerData> getTopPlayersByMoney() {
        return PlayerDataHandler.getInstance().getCacheMap().values().stream()
                .sorted(Comparator.comparingLong(PlayerData::getMoney).reversed())
                .limit(TOP_PLAYERS)
                .collect(Collectors.toList());
    }
}
