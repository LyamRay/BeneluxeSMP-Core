package me.lyamray.beneluxesmpcore.handlers.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HologramHandler {

    @Getter
    private static final HologramHandler instance = new HologramHandler();

    private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private final Map<String, Hologram> holograms = new HashMap<>();


    public void createHologram(String name, Location location, String... lines) {

        if (!manager.isLoaded()) return;
        if (manager.getHologram(name).isPresent()) return;

        TextHologramData textHologramData = new TextHologramData(name, location);

        Arrays.stream(lines).forEach(textHologramData::addLine);

        Hologram hologram = manager.create(textHologramData);
        manager.addHologram(hologram);

        holograms.put(name, hologram);
    }

    public void updateHologram(String name, String... newLines) {
        Hologram hologram = holograms.get(name);
        if (hologram == null) {
            log.warn("Hologram '{}' not found, cannot update.", name);
            return;
        }

        if (!(hologram.getData() instanceof TextHologramData data)) {
            log.warn("Hologram '{}' is not a text hologram.", name);
            return;
        }

        data.getText().clear();
        for (String line : newLines) {
            data.getText().add(line);
        }

        hologram.forceUpdate();
        log.debug("Updated hologram '{}'.", name);
    }

    public void removeHologram(String name) {
        Hologram hologram = holograms.remove(name);
        if (hologram != null) {
            manager.removeHologram(hologram);
            log.info("Removed hologram '{}'.", name);
        }
    }
}
