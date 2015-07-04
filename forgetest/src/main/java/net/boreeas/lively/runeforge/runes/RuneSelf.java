package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Malte Schütze
 */
public class RuneSelf extends Rune {
    public RuneSelf() {
        super("self",
                "  ++  +",
                "+  +  +",
                "+  ++  ");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new EffectSelf(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new EffectSelf(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new EffectSelf(zone, modTarget);
    }

    public static class EffectSelf extends Effect {

        private SelfFilter filter = new SelfFilter();

        public EffectSelf(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public EffectSelf(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
        }

        public EffectSelf(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);
            modTarget.addFilter(filter);
        }

        @Override
        public void unmodify(@NotNull Effect effect) {
            effect.removeFilter(filter);
        }

        @Override
        public Set<EffectTarget> getEffectTargets() {
            return Collections.emptySet();
        }

        private class SelfFilter implements Predicate<Object> {

            @Override
            public boolean test(Object o) {
                return getAssociatedRuneZone().getCreator().equals(o) != isNegated();
            }
        }
    }
}
