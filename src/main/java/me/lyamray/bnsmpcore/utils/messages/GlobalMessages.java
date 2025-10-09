package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.function.Supplier;

@Getter
public enum GlobalMessages {

    SCOREBOARD_TITLE(() ->
            MiniMessage.deserializeMessage("<gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient><gray>"));

    private final Supplier<Component> messageSupplier;

    GlobalMessages(Supplier<Component> messageSupplier) {
        this.messageSupplier = messageSupplier;
    }

    public Component getMessage() {
        return messageSupplier.get();
    }
}
