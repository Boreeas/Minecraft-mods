package net.boreeas.lively.runeforge;

import net.boreeas.lively.Lively;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

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

    private Set<Effect> modificators = new HashSet<>();
    private RuneZone associatedRuneZone;
    private Optional<EffectZone> associatedEffectZone = Optional.empty();
    private Optional<Effect> modTarget = Optional.empty();
    private boolean isNegated;

    private Set<Predicate<Object>> filters = new HashSet<>();

    public Effect(@NotNull RuneZone assosciatedRune) {
        this.associatedRuneZone = assosciatedRune;
    }

    public Effect(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
        this(associatedRuneZone);
        this.associatedEffectZone = Optional.of(effectZone);
    }

    public Effect(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
        this(associatedRuneZone);
        this.modTarget = Optional.of(modTarget);
    }

    public void addModification(@NotNull Effect effect) {
        modificators.add(effect);
    }

    public abstract Set<EffectTarget> getEffectTargets();

    public boolean applyToPlayer(@NotNull EntityPlayer player, int effectStrength) {
        boolean result = false;
        for (Effect effect: modificators) result |= effect.applyToPlayer(player, effectStrength);
        return result;
    }

    public boolean applyToLiving(@NotNull EntityLivingBase living, int effectStrength) {
        boolean result = false;
        for (Effect effect: modificators) result |= effect.applyToLiving(living, effectStrength);
        return result;
    }

    public boolean applyToEntity(@NotNull Entity entity, int effectStrength) {
        boolean result = false;
        for (Effect effect: modificators) result |= effect.applyToEntity(entity, effectStrength);
        return result;
    }

    public boolean applyToWorldOnce(@NotNull World world, int effectStrength) {
        boolean result = false;
        for (Effect effect: modificators) result |= effect.applyToWorldOnce(world, effectStrength);
        return result;
    }

    public boolean applyToWorldPeriodically(@NotNull World world, int effectStrength) {
        boolean result = false;
        for (Effect effect: modificators) result |= effect.applyToWorldPeriodically(world, effectStrength);
        return result;
    }

    public void modify(@NotNull Effect other) {}

    public void remove() {
        for (Effect effect: modificators) effect.remove();

        if (modTarget.isPresent()) {
            unmodify(modTarget.get());
            modTarget.get().removeModification(this);
        }

        if (associatedEffectZone.isPresent()) {
            Lively.INSTANCE.effectZoneLookup.remove(associatedEffectZone.get());
        }

        Lively.INSTANCE.runeZoneLookup.remove(associatedRuneZone);
    }

    private void removeModification(@NotNull Effect effect) {
        modificators.remove(effect);
    }

    public void unmodify(@NotNull Effect effect) {

    }

    public Optional<EffectZone> getEffectZone() {
        if (associatedEffectZone.isPresent()) return associatedEffectZone;
        if (modTarget.isPresent()) return modTarget.get().getEffectZone();

        return Optional.empty();
    }

    public boolean isNegated() {
        return isNegated;
    }

    public void setNegated(boolean negated) {
        if (this.isNegated != negated) flipNegated();
    }

    public void flipNegated() {
        this.isNegated = !isNegated;
    }

    protected boolean isDisabledByFilter(@NotNull Object obj) {
        if (filters.isEmpty()) {
            // No filters means apply to anything
            return false;
        } else {
            // Filters means apply only to those that pass the filter
            for (Predicate<Object> pred: filters) {
                if (pred.test(obj)) return false;
            }

            return true;
        }
    }

    public @NotNull Optional<Effect> getModTarget() {
        return modTarget;
    }

    public void addFilter(@NotNull Predicate<Object> filter) {
        this.filters.add(filter);
    }

    public void removeFilter(Predicate<Object> filter) {
        this.filters.remove(filter);
    }

    public @NotNull RuneZone getAssociatedRuneZone() {
        return associatedRuneZone;
    }
}
