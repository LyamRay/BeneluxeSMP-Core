package me.lyamray.beneluxesmpcore.handlers.holograms;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.data.ItemHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class HologramHandler {

    @Getter
    private static final HologramHandler instance = new HologramHandler();

    private final HologramManager manager = FancyHologramsPlugin.get().getHologramManager();
    private final Map<String, Hologram> holograms = new ConcurrentHashMap<>();

    public void createTextHologram(String name, Location location, String... lines) {
        if (isValidForCreation(name)) return;

        TextHologramData data = new TextHologramData(name, location);
        Arrays.stream(lines).forEach(data::addLine);

        addHologram(name, data);
    }

    public void createItemHologram(String name, Location location, ItemStack item, Vector3f scale) {
        if (isValidForCreation(name)) return;

        ItemHologramData data = new ItemHologramData(name, location);
        data.setItemStack(item);

        data.setScale(scale);

        addHologram(name, data);
    }

    private boolean isValidForCreation(String name) {
        if (!manager.isLoaded()) return true;

        if (manager.getHologram(name).isPresent()) {
            log.warn("Hologram '{}' already exists, skipping creation.", name);
            return true;
        }
        return false;
    }

    private void addHologram(String name, Object data) {
        Hologram hologram = manager.create((HologramData) data);
        manager.addHologram(hologram);
        holograms.put(name, hologram);
        log.debug("Created hologram '{}'.", name);
    }

    public void updateTextHologram(String name, String... newLines) {
        getHologramData(name, TextHologramData.class)
                .ifPresent(data -> {
                    data.getText().clear();
                    Arrays.stream(newLines).forEach(data::addLine);
                    forceUpdate(name);
                    log.debug("Updated text hologram '{}'.", name);
                });
    }

    public void updateItemHologram(String name, ItemStack newItem) {
        getHologramData(name, ItemHologramData.class)
                .ifPresent(data -> {
                    data.setItemStack(newItem);
                    forceUpdate(name);
                    log.debug("Updated item hologram '{}'.", name);
                });
    }

    public void updateHologramLocation(String name, Location newLocation) {
        Optional.ofNullable(holograms.get(name))
                .map(Hologram::getData)
                .ifPresent(data -> {
                    data.setLocation(newLocation);
                    forceUpdate(name);
                    log.debug("Moved hologram '{}' to new location.", name);
                });
    }
    public void removeHologram(String name) {
        Optional.ofNullable(holograms.remove(name))
                .ifPresent(hologram -> {
                    manager.removeHologram(hologram);
                    log.info("Removed hologram '{}'.", name);
                });
    }

    public Hologram getHologram(String name) {
        return holograms.get(name);
    }

    private <T extends HologramData> Optional<T> getHologramData(String name, Class<T> type) {
        return Optional.ofNullable(holograms.get(name))
                .map(Hologram::getData)
                .filter(type::isInstance)
                .map(type::cast);
    }

    public void forceUpdate(String name) {
        Optional.ofNullable(holograms.get(name)).ifPresent(Hologram::forceUpdate);
    }
}
