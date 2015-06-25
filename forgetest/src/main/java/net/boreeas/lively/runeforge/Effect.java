package net.boreeas.lively.runeforge;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public abstract class Effect {
    public enum EffectTarget {
        PLAYER,
        ENTITY_LIVING,
        ENTITY,
        WORLD_ONCE,
        WORLD_PERIODICALLY
    }

    private Set<Effect> overrides = new HashSet<>();
    public void addOverride(@NotNull Effect effect) {
        overrides.add(effect);
    }

    public abstract EffectTarget getEffectType();

    public boolean applyToPlayer(@NotNull EntityPlayer player, int effectStrength) {
        boolean result = false;
        for (Effect effect: overrides) result |= effect.applyToPlayer(player, effectStrength);
        return result;
    }

    public boolean applyToLiving(@NotNull EntityLivingBase living, int effectStrength) {
        boolean result = false;
        for (Effect effect: overrides) result |= effect.applyToLiving(living, effectStrength);
        return result;
    }

    public boolean applyToEntity(@NotNull Entity entity, int effectStrength) {
        boolean result = false;
        for (Effect effect: overrides) result |= effect.applyToEntity(entity, effectStrength);
        return result;
    }

    public boolean applyToWorldOnce(@NotNull World world, int effectStrength) {
        boolean result = false;
        for (Effect effect: overrides) result |= effect.applyToWorldOnce(world, effectStrength);
        return result;
    }

    public boolean applyToWorldPeriodically(@NotNull World world, int effectStrength) {
        boolean result = false;
        for (Effect effect: overrides) result |= effect.applyToWorldPeriodically(world, effectStrength);
        return result;
    }

    public void modify(@NotNull Effect other) {}
}
