package me.lyamray.bnsmpcore.utils.handlers.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.lyamray.bnsmpcore.BeneluxeSMPCore;
import me.lyamray.bnsmpcore.data.player.PlayerData;
import me.lyamray.bnsmpcore.data.player.PlayerDataHandler;
import me.lyamray.bnsmpcore.utils.messages.MiniMessage;
import me.lyamray.bnsmpcore.utils.numbers.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

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
        log.info("[RichestPlayers] Started hologram update task ({} ticks).", intervalTicks);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
            log.info("[RichestPlayers] Stopped hologram update task.");
        }
    }

    public void updateRichestPlayersHologram() {
        var world = Bukkit.getWorld(SPAWN_WORLD);
        if (world == null) return;

        List<PlayerData> topPlayers = getTopPlayersByMoney();

        if (topPlayers.isEmpty()) return;

        List<String> lines = new ArrayList<>(TOP_PLAYERS + 1);
        lines.add(MiniMessage.serializeComponent(MiniMessage.deserializeMessage("Rijkste Spelers:")));

        for (int i = 0; i < topPlayers.size(); i++) {
            PlayerData data = topPlayers.get(i);
            String name = Optional.ofNullable(data.getName()).filter(n -> !n.equals("Unknown")).orElse("Onbekend");
            String line = (i + 1) + " " + name + " - " + NumberFormat.formatNumber(data.getMoney());
            lines.add(MiniMessage.serializeComponent(MiniMessage.deserializeMessage(line)));
        }

        TextHologramData hologramData = getOrCreateHologramData(world);
        if (hologramData == null) return;

        hologramData.getText().clear();
        hologramData.getText().addAll(lines);
        updateHologram();
    }


    private TextHologramData getOrCreateHologramData(World world) {
        if (!FancyHologramsPlugin.isEnabled()) return null;

        FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
        Hologram hologram = plugin.getHologramManager().getHologram(HOLOGRAM_NAME).orElse(null);

        if (hologram != null && hologram.getData() instanceof TextHologramData existingData) {
            return existingData;
        }

        Location location = new Location(world, 0, 100, 0);
        TextHologramData newData = new TextHologramData(HOLOGRAM_NAME, location);
        newData.setBillboard(org.bukkit.entity.Display.Billboard.CENTER);
        newData.setTextAlignment(org.bukkit.entity.TextDisplay.TextAlignment.CENTER);
        newData.setPersistent(true);

        Hologram newHologram = plugin.getHologramManager().create(newData);
        plugin.getHologramManager().addHologram(newHologram);

        try {
            plugin.getHologramStorage().save(newHologram);
        } catch (Exception e) {
            log.error("[RichestPlayers] Failed to save hologram '{}'", HOLOGRAM_NAME, e);
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
