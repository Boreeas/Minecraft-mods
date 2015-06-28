package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import org.jetbrains.annotations.NotNull;

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

            if (modTarget instanceof RuneHealth.HealthEffect) {
                System.out.println("modifying health rune");
                ((RuneHealth.HealthEffect) modTarget).amt *= -1;
                System.out.println("new heal amt " + ((RuneHealth.HealthEffect) modTarget).amt);
            }
        }

        @Override
        public EffectTarget getEffectType() {
            return EffectTarget.WORLD_ONCE;
        }

        @Override
        public void unmodify(@NotNull Effect effect) {
            if (effect instanceof RuneHealth.HealthEffect) {
                ((RuneHealth.HealthEffect) effect).amt *= -1;
            }
        }
    }
}
