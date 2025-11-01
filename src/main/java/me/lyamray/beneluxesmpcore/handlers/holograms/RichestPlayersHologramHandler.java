package me.lyamray.beneluxesmpcore.handlers.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
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
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
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
                .toList();

        List<String> lines = new ArrayList<>();
        lines.add(MiniMessage.serializeComponent(MiniMessage.deserializeMessage(
                "<gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>Top 10 Rijkste Spelers</b>:</gradient><gray>"
        )));
        lines.addAll(playerLines);

        TextHologramData hologramData = getOrCreateHologramData(world);
        if (hologramData == null) return;

        hologramData.getText().clear();
        hologramData.getText().addAll(lines);
        hologramData.setVisibilityDistance(1000);
        updateHologram();
    }

    private TextHologramData getOrCreateHologramData(World world) {
        if (!FancyHologramsPlugin.isEnabled()) return null;

        FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
        Hologram hologram = plugin.getHologramManager().getHologram(HOLOGRAM_NAME).orElse(null);

        if (hologram != null && hologram.getData() instanceof TextHologramData existingData) {
            return existingData;
        }

        Location location = new Location(world, -1, 100, -7).toCenterLocation();
        TextHologramData newData = new TextHologramData(HOLOGRAM_NAME, location);
        newData.setBillboard(Display.Billboard.CENTER);
        newData.setTextAlignment(TextDisplay.TextAlignment.CENTER);
        newData.setPersistent(true);

        Hologram newHologram = plugin.getHologramManager().create(newData);
        plugin.getHologramManager().addHologram(newHologram);

        try {
            plugin.getHologramStorage().save(newHologram);
        } catch (Exception e) {
            log.error("Failed to save hologram '{}'", HOLOGRAM_NAME, e);
        }

        return newData;
    }

    private void updateHologram() {
        FancyHologramsPlugin.get().getHologramManager().getHologram(HOLOGRAM_NAME)
                .ifPresent(h -> {
                    h.forceUpdate();
                    h.queueUpdate();
                });
    }

    public void removeHologram() {
        FancyHologramsPlugin.get().getHologramManager().getHologram(HOLOGRAM_NAME)
                .ifPresent(FancyHologramsPlugin.get().getHologramManager()::removeHologram);
    }

    private List<PlayerData> getTopPlayersByMoney() {
        return PlayerDataHandler.getInstance().getCacheMap().values().stream()
                .sorted(Comparator.comparingLong(PlayerData::getMoney).reversed())
                .limit(TOP_PLAYERS)
                .collect(Collectors.toList());
    }
}
