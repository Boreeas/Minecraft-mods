package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.passive.EntityWolf;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * @author Malte Schütze
 */
public class RuneHostile extends Rune {
    public RuneHostile() {
        super("hostile",
                "++ ++",
                "+   +",
                "     ",
                "+   +",
                "++ ++");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new EffectHostile(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new EffectHostile(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new EffectHostile(zone, modTarget);
    }

    public static class EffectHostile extends Effect {
        private HostileFilter filter = new HostileFilter();

        public EffectHostile(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public EffectHostile(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
        }

        public EffectHostile(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);
            modTarget.addFilter(filter);
        }

        @Override
        public void unmodify(@NotNull Effect effect) {
            effect.removeFilter(filter);
        }

        @Override
        public EffectTarget getEffectType() {
            return EffectTarget.WORLD_ONCE;
        }

        private class HostileFilter implements Predicate<Object> {

            @Override
            public boolean test(Object o) {
                return ((o instanceof Entity && ((Entity) o).isCreatureType(EnumCreatureType.monster, false))
                        || (o instanceof EntityWolf && ((EntityWolf) o).isAngry()))

                        != isNegated();
            }
        }
    }
}
