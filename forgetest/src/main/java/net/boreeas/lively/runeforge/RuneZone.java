package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.GlobalCoord;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class RuneZone extends Zone {

    private Effect associatedEffect;

    public RuneZone(@NotNull GlobalCoord coords, int radius) {
        super(coords, radius);
    }

    public RuneZone(@NotNull GlobalCoord coords, int radius, @NotNull Effect associatedEffect) {
        super(coords, radius);
        this.associatedEffect = associatedEffect;
    }

    public void setAssociatedEffect(Effect associatedEffect) {
        // Delayed assignment during construction
        if (this.associatedEffect != null) throw new IllegalStateException("already assigned");
        this.associatedEffect = associatedEffect;
    }

    public @NotNull Effect getAssociatedEffect() {
        return associatedEffect;
    }
}
