package net.boreeas.lively.runeforge;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.boreeas.lively.util.GlobalCoord;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Malte Schütze
 */
public class ZoneLookup {
    private LoadingCache<GlobalCoord, Set<EffectZone>> entityTargetedZonesByChunk = CacheBuilder.newBuilder().build(new SetCacheLoader());
    private LoadingCache<GlobalCoord, Set<EffectZone>> livingTargetedZonesByChunk = CacheBuilder.newBuilder().build(new SetCacheLoader());
    private LoadingCache<GlobalCoord, Set<EffectZone>> playerTargetedZonesByChunk = CacheBuilder.newBuilder().build(new SetCacheLoader());
    private LoadingCache<GlobalCoord, Set<EffectZone>> periodicallyWorldTargetedZonesByChunk = CacheBuilder.newBuilder().build(new SetCacheLoader());
    private LoadingCache<GlobalCoord, Set<EffectZone>> onceWorldTargetedZonesByChunk = CacheBuilder.newBuilder().build(new SetCacheLoader());

    private LoadingCache<GlobalCoord, Set<EffectZone>> globalZones = CacheBuilder.newBuilder().build(new SetCacheLoader());

    public void addEffectZone(EffectZone zone) throws ExecutionException {
        int minChunkX = (zone.getCoords().getX() - zone.getEffectRadius()) / 16;
        int maxChunkX = (zone.getCoords().getX() + zone.getEffectRadius()) / 16;
        int minChunkZ = (zone.getCoords().getZ() - zone.getEffectRadius()) / 16;
        int maxChunkZ = (zone.getCoords().getZ() + zone.getEffectRadius()) / 16;

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                switch (zone.getEffect().getEffectType()) {
                    case PLAYER:
                        playerTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                        break;
                    case ENTITY_LIVING:
                        livingTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                        break;
                    case ENTITY:
                        entityTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                        break;
                    case WORLD_ONCE:
                        onceWorldTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                        break;
                    case WORLD_PERIODICALLY:
                        periodicallyWorldTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                        break;
                }

                globalZones.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
            }
        }
    }

    /**
     * A coord is defined to be maybe part of a rune if it's in the same world, on the same y-level and lies within the
     * runeRadius of the rune
     * @param coord
     * @return
     */
    public boolean isCoordMaybePartOfRune(@NotNull GlobalCoord coord) {
        try {
            Set<EffectZone> zones = globalZones.get(coord);
            if (zones.isEmpty()) globalZones.invalidate(coord);

            return zones.parallelStream().anyMatch(zone -> zone.contains(coord));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    public @NotNull Optional<EffectZone> getZoneWithPosition(@NotNull GlobalCoord coord) {
        try {
            Set<EffectZone> zones = globalZones.get(coord);
            if (zones.isEmpty()) globalZones.invalidate(coord);

            return zones.parallelStream().filter(zone -> zone.contains(coord)).findAny();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void remove(@NotNull EffectZone zone) {
        int minChunkX = (zone.getCoords().getX() - zone.getEffectRadius()) / 16;
        int maxChunkX = (zone.getCoords().getX() + zone.getEffectRadius()) / 16;
        int minChunkZ = (zone.getCoords().getZ() - zone.getEffectRadius()) / 16;
        int maxChunkZ = (zone.getCoords().getZ() + zone.getEffectRadius()) / 16;

        try {
            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    switch (zone.getEffect().getEffectType()) {
                        case PLAYER:
                            playerTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                            break;
                        case ENTITY_LIVING:
                            livingTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                            break;
                        case ENTITY:
                            entityTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                            break;
                        case WORLD_ONCE:
                            onceWorldTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                            break;
                        case WORLD_PERIODICALLY:
                            periodicallyWorldTargetedZonesByChunk.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                            break;
                    }

                    globalZones.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).remove(zone);
                }
            }
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SubscribeEvent
    public void onWorldTick(@NotNull TickEvent.WorldTickEvent evt) {
        if (evt.side == Side.CLIENT) return;


        for (Set<EffectZone> worldZones: periodicallyWorldTargetedZonesByChunk.asMap().values()) {
            worldZones.forEach(zone -> zone.applyToWorldPeriodically(zone.getCoords().getWorld()));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(@NotNull TickEvent.PlayerTickEvent evt) throws ExecutionException {
        if (evt.side == Side.CLIENT) return;

        int chunkX = evt.player.chunkCoordX;
        int chunkZ = evt.player.chunkCoordZ;

        GlobalCoord targetChunk = new GlobalCoord(evt.player.worldObj, chunkX, 0, chunkZ);

        Set<EffectZone> playerZones = playerTargetedZonesByChunk.get(targetChunk);
        if (playerZones.isEmpty()) {
            playerTargetedZonesByChunk.invalidate(targetChunk);
        } else {
            for (EffectZone zone: playerZones) {
                zone.applyToPlayer(evt.player);
            }
        }

        Set<EffectZone> livingZones = livingTargetedZonesByChunk.get(targetChunk);
        if (livingZones.isEmpty()) {
            livingTargetedZonesByChunk.invalidate(targetChunk);
        } else {
            for (EffectZone zone: livingZones) {
                zone.applyToLiving(evt.player);
            }
        }

        Set<EffectZone> entityZones = entityTargetedZonesByChunk.get(targetChunk);
        if (entityZones.isEmpty()) {
            entityTargetedZonesByChunk.invalidate(targetChunk);
        } else {
            for (EffectZone zone: entityZones) {
                zone.applyToLiving(evt.player);
            }
        }
    }

    private static class SetCacheLoader extends CacheLoader<GlobalCoord, Set<EffectZone>> {
        @Override
        public Set<EffectZone> load(GlobalCoord _) throws Exception {
            return new HashSet<>();
        }
    }
}
