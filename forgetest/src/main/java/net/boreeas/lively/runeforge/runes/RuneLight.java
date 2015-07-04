package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.Lively;
import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import net.boreeas.lively.util.GlobalCoord;
import net.boreeas.lively.util.Vec3Int;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class RuneLight extends Rune {
    public RuneLight() {
        super("light",
                "+++++",
                "    +",
                "+ + +",
                "+    ",
                "+++++");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new EffectLight(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new EffectLight(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new EffectLight(zone, modTarget);
    }

    public static class EffectLight extends Effect {
        private Optional<Vec3Int> renderLoc = Optional.empty();

        public EffectLight(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public EffectLight(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
            GlobalCoord coords = effectZone.getCoords();
            Vec3Int pos = new Vec3Int(coords.getX(), coords.getY(), coords.getZ());
            this.renderLoc = Optional.of(pos);
            Lively.INSTANCE.serverRenderHandler.addLightRune(pos);
        }

        public EffectLight(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);
        }

        @Override
        public Set<EffectTarget> getEffectTargets() {
            return Collections.singleton(EffectTarget.WORLD_ONCE);
        }

        @Override
        public void remove() {
            renderLoc.ifPresent(Lively.INSTANCE.serverRenderHandler::removeLightRune);
        }
    }
}
