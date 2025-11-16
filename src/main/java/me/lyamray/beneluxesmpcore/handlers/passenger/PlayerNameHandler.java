package me.lyamray.beneluxesmpcore.handlers.passenger;

import lombok.Getter;
import lombok.Setter;
import me.lyamray.beneluxesmpcore.BeneluxeSMPCore;
import me.lyamray.beneluxesmpcore.data.player.PlayerData;
import me.lyamray.beneluxesmpcore.data.player.PlayerDataHandler;
import me.lyamray.beneluxesmpcore.utils.messages.MiniMessage;
import me.lyamray.beneluxesmpcore.utils.messages.TabMessages;
import me.lyamray.beneluxesmpcore.utils.ranks.Ranks;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class PlayerNameHandler {

    @Getter
    private static final PlayerNameHandler instance = new PlayerNameHandler();

    private Map<Player, Entity> intermediateEntities = new HashMap<>();
    private Map<Player, Pair<TextDisplay, String>> nameDisplays = new HashMap<>();

    private BukkitTask passengerTask;

    public void startPassengerCheckTask(long intervalTicks) {
        if (passengerTask != null && !passengerTask.isCancelled()) {
            passengerTask.cancel();
        }

        passengerTask = Bukkit.getScheduler().runTaskTimer(
                BeneluxeSMPCore.getInstance(),
                this::ensureAllPassengers,
                0L,
                intervalTicks
        );
    }

    public void ensureAllPassengers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ensurePassenger(player);
        }
    }

    public void ensurePassenger(Player player) {
        PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());
        if (data == null) return;

        Ranks rank = parseRank(data.getRank());
        if (rank == null) rank = Ranks.OVERLEVER;

        String expectedText = TabMessages.PLAYER_ENTRY.getPlayerMessage(rank, player);
        Pair<TextDisplay, String> pair = nameDisplays.get(player);
        Entity interaction = intermediateEntities.get(player);

        if (pair == null
                || pair.getLeft() == null
                || pair.getLeft().isDead()
                || interaction == null
                || interaction.isDead()
                || !player.getPassengers().contains(interaction)
                || !expectedText.equals(pair.getRight())) {
            updateNameFor(player);
        }
    }

    public void updateNameFor(Player player) {
        PlayerData data = PlayerDataHandler.getInstance().getData(player.getUniqueId());
        if (data == null) return;

        Ranks rank = getRankOrDefault(data);
        String displayName = TabMessages.PLAYER_ENTRY.getPlayerMessage(rank, player);
        Component nameComponent = MiniMessage.deserializeMessage(displayName);

        if (reuseExistingEntities(player, displayName, nameComponent)) return;

        remove(player);
        spawnInteractionWithText(player, nameComponent, displayName);
    }

    private Ranks getRankOrDefault(PlayerData data) {
        Ranks rank = parseRank(data.getRank());
        return rank != null ? rank : Ranks.OVERLEVER;
    }

    private boolean reuseExistingEntities(Player player, String displayName, Component nameComponent) {
        Pair<TextDisplay, String> pair = nameDisplays.get(player);
        Entity interaction = intermediateEntities.get(player);

        if (pair != null && pair.getLeft() != null && !pair.getLeft().isDead()
                && interaction != null && !interaction.isDead()
                && displayName.equals(pair.getRight())) {
            pair.getLeft().text(nameComponent);
            return true;
        }
        return false;
    }

    private void spawnInteractionWithText(Player player, Component nameComponent, String displayName) {
        Location spawnLocation = player.getLocation().toCenterLocation();

        spawnLocation.getWorld().spawn(spawnLocation, Interaction.class, interaction -> {
            setupInteraction(interaction);

            TextDisplay textDisplay = spawnTextDisplay(interaction, nameComponent);
            interaction.addPassenger(textDisplay);

            nameDisplays.put(player, Pair.of(textDisplay, displayName));
            intermediateEntities.put(player, interaction);

            player.addPassenger(interaction);
        });

        TextDisplay entity = nameDisplays.get(player).getLeft();
        player.hideEntity(BeneluxeSMPCore.getInstance(), entity);
    }


    private void setupInteraction(Interaction interaction) {
        interaction.setInteractionHeight(0.25F);
        interaction.setInteractionWidth(0.0F);
        interaction.setGravity(false);
        interaction.setInvulnerable(true);
        interaction.setInvisible(true);
    }

    private TextDisplay spawnTextDisplay(Interaction interaction, Component nameComponent) {
        TextDisplay textDisplay = interaction.getWorld().spawn(interaction.getLocation(), TextDisplay.class);
        textDisplay.text(nameComponent);
        textDisplay.setBillboard(TextDisplay.Billboard.CENTER);
        return textDisplay;
    }

    private Ranks parseRank(String rankString) {
        if (rankString == null) return null;
        try {
            return Ranks.valueOf(rankString.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    public void removeAll() {
        nameDisplays.values().forEach(pair ->
                Optional.ofNullable(pair.getLeft())
                        .filter(display -> !display.isDead())
                        .ifPresent(TextDisplay::remove)
        );
        nameDisplays.clear();

        intermediateEntities.values().forEach(entity ->
                Optional.ofNullable(entity)
                        .filter(e -> !e.isDead())
                        .ifPresent(Entity::remove)
        );
        intermediateEntities.clear();
    }

    public void remove(Player player) {
        Optional.ofNullable(nameDisplays.remove(player))
                .map(Pair::getLeft)
                .filter(display -> !display.isDead())
                .ifPresent(TextDisplay::remove);

        Optional.ofNullable(intermediateEntities.remove(player))
                .filter(entity -> !entity.isDead())
                .ifPresent(Entity::remove);
    }

    public void stopPassengerCheckTask() {
        if (passengerTask != null) {
            passengerTask.cancel();
            passengerTask = null;
        }
    }
}
