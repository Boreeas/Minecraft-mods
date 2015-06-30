package net.boreeas.lively.runeforge.runes;

import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import net.boreeas.lively.util.GlobalCoord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class RuneContain extends Rune {
    public RuneContain() {
        super("contain", " +++ ",
                         "+   +",
                         "+   +",
                         "+   +",
                         " +++ ");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new EffectContain(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new EffectContain(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new EffectContain(zone, modTarget);
    }

    public static class EffectContain extends Effect {

        public EffectContain(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
        }


        public EffectContain(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public EffectContain(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);
        }

        @Override
        public EffectTarget getEffectType() {
            return EffectTarget.ENTITY;
        }

        @Override
        public boolean applyToEntity(@NotNull Entity entity, int effectStrength) {
            if (super.applyToEntity(entity, effectStrength)) return true;
            if (isDisabledByFilter(entity)) return false;
            if (!getEffectZone().isPresent()) return false;

            GlobalCoord center = getEffectZone().get().getCoords();

            double dx = entity.posX - center.getX();
            double dz = entity.posZ - center.getZ();

            double dist = Math.sqrt(dx*dx + dz*dz);
            int effectRadius = getEffectZone().get().getRadius();

            double pushStrength = (effectRadius - dist) / (0.001 + dist);
            pushStrength = Math.min(pushStrength, 0.5 * effectStrength);

            entity.motionX += pushStrength * dx / (Math.abs(dx) + Math.abs(dz));
            entity.motionZ += pushStrength * dz / (Math.abs(dx) + Math.abs(dz));
            //entity.addVelocity(entity.motionX, 0, entity.motionZ);

            if (entity instanceof EntityPlayerMP) {
                S12PacketEntityVelocity packet = new S12PacketEntityVelocity(entity.getEntityId(), entity.motionX, 0, entity.motionZ);
                ((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(packet);
            }
            return true;
        }
    }
}
