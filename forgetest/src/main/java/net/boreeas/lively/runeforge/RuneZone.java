package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.GlobalCoord;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class RuneZone extends Zone {

    private final Effect associatedEffect;

    public RuneZone(@NotNull GlobalCoord coords, int radius, @NotNull Effect associatedEffect) {
        super(coords, radius);
        this.associatedEffect = associatedEffect;
    }

    public @NotNull Effect getAssociatedEffect() {
        return associatedEffect;
    }
}
