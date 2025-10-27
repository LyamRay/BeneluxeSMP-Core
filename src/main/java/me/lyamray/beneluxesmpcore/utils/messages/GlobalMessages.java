package me.lyamray.beneluxesmpcore.utils.messages;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public enum GlobalMessages {

    BENELUXE_TITLE(() ->
            "<gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient><gray>");

    private final Supplier<String> messageSupplier;

    GlobalMessages(Supplier<String> messageSupplier) {
        this.messageSupplier = messageSupplier;
    }

    public String getMessage() {
        return messageSupplier.get();
    }
}

