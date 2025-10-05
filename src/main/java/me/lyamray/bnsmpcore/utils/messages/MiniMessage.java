package me.lyamray.bnsmpcore.utils.messages;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@UtilityClass
public class MiniMessage {
    private static final net.kyori.adventure.text.minimessage.MiniMessage miniMessage = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();

    public Component deserializeMessage(String input) {
        return miniMessage.deserialize(input);
    }

    public void sendMessage(String input, Player player) {
        player.sendRichMessage(input);
    }

    public String serializeComponent(Component component) {
        return miniMessage.serialize(component);
    }

    public void clearChat(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage(Component.empty());
        }
    }
}