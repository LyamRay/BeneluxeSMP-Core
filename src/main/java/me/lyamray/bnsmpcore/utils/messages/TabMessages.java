package me.lyamray.bnsmpcore.utils.messages;

import lombok.Getter;

import java.util.function.Function;

@Getter
public enum TabMessages {

    HEADER(aantal -> ("""
            
            <gray> • </gray><gradient:#BFE7EA:#A4D0E1:#BFE7EA><b>BeneluxeSMP</b></gradient>
            
            <gradient:#D2E3E6:#D2E3E6>Speler </gradient><gradient:#C6E5F1:#C4D0CD>aantal: {aantal}</gradient>
            """).replace("{aantal}", aantal.toString())),

    FOOTER(aantal -> ("""
            
            <gradient:#D2E3E6:#D2E3E6>play.</gradient><gradient:#C6E5F1:#C4D0CD>beneluxesmp.be!</gradient>
            """));

    private final Function<Integer, String> messageFunction;

    TabMessages(Function<Integer, String> messageFunction) {
        this.messageFunction = messageFunction;
    }

    public String getMessage(int aantal) {
        return messageFunction.apply(aantal);
    }
}
