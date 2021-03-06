package net.boreeas.lively.runeforge.runes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import net.boreeas.lively.util.GlobalCoord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Malte Sch�tze
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

        Cache<Entity, GlobalCoord> cachedPos = CacheBuilder.newBuilder().expireAfterAccess(4, TimeUnit.SECONDS).build();

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
        public Set<EffectTarget> getEffectTargets() {
            return Collections.singleton(EffectTarget.ENTITY);
        }

        @Override
        public boolean applyToEntity(@NotNull Entity entity, int effectStrength) {
            if (super.applyToEntity(entity, effectStrength)) return true;
            if (isDisabledByFilter(entity)) return false;
            if (!getEffectZone().isPresent()) return false;

            if (isNegated()) {
                exclude(entity, effectStrength);
            } else {
                contain(entity, effectStrength);
            }

            return true;
        }

        private void contain(Entity entity, int effectStrength) {
            GlobalCoord center = getEffectZone().get().getCoords();
            int radius = getEffectZone().get().getRadius();

            double dx = entity.posX - center.getX();
            double dz = entity.posZ - center.getZ();

            double distSq = dx*dx + dz*dz;
            int radSq = radius * radius;
            if (distSq < (radSq - 4*radius - 4)) return;


            double pushStrength = (1-(radSq-distSq)) / (radSq - distSq + 0.001);
            pushStrength = Math.max(Math.min(pushStrength, 1), -1);

            entity.motionX += pushStrength * dx / (Math.abs(dx) + Math.abs(dz));
            entity.motionZ += pushStrength * dz / (Math.abs(dx) + Math.abs(dz));

            if (entity instanceof EntityPlayerMP) {
                S12PacketEntityVelocity packet = new S12PacketEntityVelocity(entity.getEntityId(), entity.motionX, 0, entity.motionZ);
                ((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(packet);
            }
        }

        private void exclude(Entity entity, int effectStrength) {
            GlobalCoord center = getEffectZone().get().getCoords();

            double dx = entity.posX - center.getX();
            double dz = entity.posZ - center.getZ();

            double dist = Math.sqrt(dx*dx + dz*dz);
            int effectRadius = getEffectZone().get().getRadius();

            double pushStrength = (effectRadius - dist) / (0.001 + dist);
            pushStrength = Math.min(pushStrength, 2 * effectStrength);

            entity.motionX += pushStrength * dx / (Math.abs(dx) + Math.abs(dz));
            entity.motionZ += pushStrength * dz / (Math.abs(dx) + Math.abs(dz));

            if (entity instanceof EntityPlayerMP) {
                S12PacketEntityVelocity packet = new S12PacketEntityVelocity(entity.getEntityId(), entity.motionX, 0, entity.motionZ);
                ((EntityPlayerMP) entity).playerNetServerHandler.sendPacket(packet);
            }
        }
    }
}
