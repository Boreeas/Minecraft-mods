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
public class EffectZone {
    private Effect effect;
    private int effectStrength;
    private GlobalCoord coords;
    private int effectRadius;
    private int runeRadius;

    public EffectZone(@NotNull Effect effect, @NotNull GlobalCoord coords, int effectStrength, int effectRadius, int runeRadius) {
        this.effect = effect;
        this.coords = coords;
        this.effectStrength = effectStrength;
        this.effectRadius = effectRadius;
        this.runeRadius = runeRadius;
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

    public @NotNull GlobalCoord getCoords() {
        return coords;
    }

    /**
     * Return the radius of the circle in which this rune has an effect. This is always guaranteed to be larger or equal
     * to the rune radius
     * @return
     */
    public int getEffectRadius() {
        return effectRadius;
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

    /**
     * Return the rune radius (the radius of the circle that this rune physically occupies)
     * @return the rune radius
     */
    public int getRuneRadius() {
        return runeRadius;
    }

    public boolean contains(@NotNull GlobalCoord coords) {
        if (coords.getWorld() != this.coords.getWorld() || coords.getY() != this.coords.getY()) {
            return false;
        }

        int dx = coords.getX() - this.coords.getX();
        int dz = coords.getZ() - this.coords.getZ();

        return dx*dx + dz*dz < runeRadius*runeRadius;
    }
}
