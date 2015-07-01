package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.GlobalCoord;
import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class RuneZone extends Zone {

    private Effect associatedEffect;
    private EntityPlayer creator;

    public RuneZone(@NotNull GlobalCoord coords, int radius, @NotNull EntityPlayer creator) {
        super(coords, radius);
        this.creator = creator;
    }

    public RuneZone(@NotNull GlobalCoord coords, int radius, @NotNull Effect associatedEffect, @NotNull EntityPlayer creator) {
        super(coords, radius);
        this.associatedEffect = associatedEffect;
        this.creator = creator;
    }

    public void setAssociatedEffect(Effect associatedEffect) {
        // Delayed assignment during construction
        if (this.associatedEffect != null) throw new IllegalStateException("already assigned");
        this.associatedEffect = associatedEffect;
    }

    public @NotNull Effect getAssociatedEffect() {
        return associatedEffect;
    }

    public @NotNull EntityPlayer getCreator() {
        return creator;
    }
}
