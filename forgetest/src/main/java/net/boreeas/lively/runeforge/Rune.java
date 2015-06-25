package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public abstract class Rune {
    private String name;
    private boolean[][] flags;

    public Rune(@NotNull String name, @NotNull String... format) {
        this.name = name;
        flags = new boolean[format.length][format[0].length()];

        for (int i = 0; i < format.length; i++) {
            for (int j = 0; j < format[i].length(); j++) {
                flags[i][j] = (format[i].charAt(j) != ' ');
            }
        }

        this.flags = ArrayUtil.trim(flags);
    }

    public abstract @NotNull Effect makeEffect(@NotNull RuneZone zone);
    public abstract @NotNull Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone);
    public abstract @NotNull Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget);

    public @NotNull String getName() {
        return name;
    }

    public int width() {
        return flags[0].length;
    }

    public int height() {
        return flags.length;
    }

    public boolean isSet(int x, int z) {
        return flags[z][x];
    }
}
