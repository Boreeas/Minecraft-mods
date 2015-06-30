package net.boreeas.lively.runeforge;

import net.boreeas.lively.Lively;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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

    /**
     * Filter to which entities the effect should be granted. Grant or deny depends on {@link #filterType}
     */
    public Predicate<Object> filter = e -> false;
    /**
     * Filter behavior. {@link FilterType#DENY_ON_PASS} denies the effect
     * to all
     */
    public FilterType filterType = FilterType.DENY_ON_PASS;

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

    public abstract EffectTarget getEffectType();

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
        this.isNegated = negated;
    }

    public void flipNegated() {
        this.isNegated = !isNegated;
    }

    protected boolean isDisabledByFilter(Object p) {
        boolean filterPass = filter.test(p);
        return (filterPass && filterType == FilterType.DENY_ON_PASS) || (!filterPass && filterType == FilterType.APPLY_ON_PASS);
    }

    public Optional<Effect> getModTarget() {
        return modTarget;
    }

    public enum FilterType {APPLY_ON_PASS, DENY_ON_PASS}
}
