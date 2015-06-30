package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.GlobalCoord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class EffectZone extends Zone {
    private Effect effect;
    private int effectStrength;

    public EffectZone(@NotNull Effect effect, @NotNull GlobalCoord coords, int effectStrength, int effectRadius) {
        super(coords, effectRadius);
        this.effect = effect;
        this.effectStrength = effectStrength;
    }

    public EffectZone(@NotNull GlobalCoord coords, int effectStrength, int effectRadius) {
        super(coords, effectRadius);
        this.effectStrength = effectStrength;
    }

    public void applyToPlayer(@NotNull EntityPlayer player) {
        effect.applyToPlayer(player, effectStrength);
    }

    public void applyToEntity(@NotNull Entity entity) {
        effect.applyToEntity(entity, effectStrength);
    }

    public void applyToLiving(@NotNull EntityLivingBase living) {
        effect.applyToLiving(living, effectStrength);
    }

    public void applyToWorldOnce(@NotNull World world) {
        effect.applyToWorldOnce(world, effectStrength);
    }

    public void applyToWorldPeriodically(@NotNull World world) {
        effect.applyToWorldPeriodically(world, effectStrength);
    }

    /**
     * Return the effect of this rune
     * @return
     */
    public @NotNull Effect getEffect() {
        return effect;
    }

    public int getEffectStrength() {
        return effectStrength;
    }

    public void setAssociatedEffect(Effect associatedEffect) {
        // delayed assignment during construction
        if (this.effect != null) throw new IllegalStateException("already assigned");
        this.effect = associatedEffect;
    }

    public boolean contains(@NotNull GlobalCoord coords) {
        if (coords.getWorld() != getCoords().getWorld()) {
            return false;
        }

        int dx = coords.getX() - getCoords().getX();
        int dz = coords.getZ() - getCoords().getZ();

        return dx*dx + dz*dz <= getRadius()*getRadius();
    }
}
