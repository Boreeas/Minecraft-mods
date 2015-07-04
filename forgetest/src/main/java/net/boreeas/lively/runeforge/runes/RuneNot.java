package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class RuneNot extends Rune {
    public RuneNot() {
        super("not", "   +   ",
                     "       ",
                     "+++++++");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new NegateEffect(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new NegateEffect(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new NegateEffect(zone, modTarget);
    }

    public static class NegateEffect extends Effect {

        public NegateEffect(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public NegateEffect(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
        }

        public NegateEffect(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);

            modTarget.flipNegated();
        }

        @Override
        public Set<EffectTarget> getEffectTargets() {
            return Collections.emptySet();
        }

        @Override
        public void unmodify(@NotNull Effect effect) {
            effect.flipNegated();
        }

        @Override
        public void flipNegated() {
            super.flipNegated();
            getModTarget().ifPresent(Effect::flipNegated);
        }
    }
}
