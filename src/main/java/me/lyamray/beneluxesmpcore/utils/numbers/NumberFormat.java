package me.lyamray.beneluxesmpcore.utils.numbers;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

@UtilityClass
public class NumberFormat {

    private static final String[] SUFFIXES = {"", "k", "m", "b", "t"};

    public String formatNumber(double number) {
        if (number < 1000) {
            return String.valueOf((int) number);
        }

        int magnitude = (int) (Math.log10(number) / 3);
        if (magnitude >= SUFFIXES.length) {
            magnitude = SUFFIXES.length - 1;
        }

        double scaled = number / Math.pow(1000, magnitude);

        DecimalFormat df = new DecimalFormat(scaled >= 100 ? "0" : (scaled >= 10 ? "0.#" : "0.##"));

        return df.format(scaled) + SUFFIXES[magnitude];
    }
}
