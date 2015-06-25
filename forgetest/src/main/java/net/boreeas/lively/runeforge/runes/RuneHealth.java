package net.boreeas.lively.runeforge.runes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.boreeas.lively.runeforge.Effect;
import net.boreeas.lively.runeforge.EffectZone;
import net.boreeas.lively.runeforge.Rune;
import net.boreeas.lively.runeforge.RuneZone;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author Malte Schütze
 */
public class RuneHealth extends Rune {
    public RuneHealth() {
        super("health", "++++  ",
                        "+  +  ",
                        "++++++",
                        "    + ");
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone) {
        return new HealthEffect(zone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull EffectZone associatedEffectZone) {
        return new HealthEffect(zone, associatedEffectZone);
    }

    @NotNull
    @Override
    public Effect makeEffect(@NotNull RuneZone zone, @NotNull Effect modTarget) {
        return new HealthEffect(zone, modTarget);
    }

    public class HealthEffect extends Effect {
        /**
         * Filter to which entities the effect should be granted. Grant or deny depends on {@link #applicationType}
         */
        public Predicate<EntityLiving> filter = e -> false;
        /**
         * Filter behavior. {@link net.boreeas.lively.runeforge.runes.RuneHealth.ApplicationType#DENY_ON_PASS} denies the effect
         * to all
         */
        public ApplicationType applicationType = ApplicationType.DENY_ON_PASS;
        /**
         * Heal amount in half hears
         */
        public int amt = 1;
        /**
         * Cooldown in milliseconds
         */
        private int cooldown = 1000;
        private Cache<EntityLivingBase, EntityLivingBase> cache = makeCacheBuilder();

        public HealthEffect(@NotNull RuneZone assosciatedRune) {
            super(assosciatedRune);
        }

        public HealthEffect(@NotNull RuneZone associatedRuneZone, @NotNull EffectZone effectZone) {
            super(associatedRuneZone, effectZone);
        }

        public HealthEffect(@NotNull RuneZone associatedRuneZone, @NotNull Effect modTarget) {
            super(associatedRuneZone, modTarget);
        }

        /**
         * Returns the cooldown between successive applications in milliseconds
         * @return The cooldown
         */
        public int getCooldown() { return cooldown; }

        /**
         * Set the cooldown between applications in milliseconds
         * @param cooldown The cooldown between applications
         */
        public void setCooldown(int cooldown) {
            this.cooldown = cooldown;
            this.cache = makeCacheBuilder();
        }

        private Cache<EntityLivingBase, EntityLivingBase> makeCacheBuilder() {
            return CacheBuilder.newBuilder().expireAfterWrite(cooldown, TimeUnit.MILLISECONDS).build();
        }

        @Override
        public boolean applyToLiving(@NotNull EntityLivingBase living, int effectStrength) {
            if (cache.getIfPresent(living) != null) return false;
            cache.put(living, living);

            if (super.applyToLiving(living, effectStrength)) return true;

            living.heal(amt * effectStrength);

            Random random = living.worldObj.rand;
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            living.worldObj.spawnParticle("heart", living.posX + random.nextDouble()*2 - 0.5, living.posY, living.posZ + random.nextDouble()*2 - 0.5,
                    random.nextDouble(), 1, random.nextDouble());
            return true;
        }

        @Override
        public EffectTarget getEffectType() {
            return EffectTarget.ENTITY_LIVING;
        }
    }

    public enum ApplicationType {APPLY_ON_PASS, DENY_ON_PASS}
}
