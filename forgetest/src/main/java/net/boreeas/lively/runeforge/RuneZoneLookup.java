package net.boreeas.lively.runeforge;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import net.boreeas.lively.util.GlobalCoord;
import net.boreeas.lively.util.SetCacheLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @author Malte Schütze
 */
public class RuneZoneLookup {
    private LoadingCache<GlobalCoord, Set<RuneZone>> zones = CacheBuilder.newBuilder().build(new SetCacheLoader<>());

    public void add(RuneZone zone) {
        int minChunkX = (zone.getCoords().getX() - zone.getRadius()) / 16;
        int maxChunkX = (zone.getCoords().getX() + zone.getRadius()) / 16;
        int minChunkZ = (zone.getCoords().getZ() - zone.getRadius()) / 16;
        int maxChunkZ = (zone.getCoords().getZ() + zone.getRadius()) / 16;

        try {
            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                        zones.get(new GlobalCoord(zone.getCoords().getWorld(), x, 0, z)).add(zone);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void remove(@NotNull RuneZone zone) {
        int minChunkX = (zone.getCoords().getX() - zone.getRadius()) / 16;
        int maxChunkX = (zone.getCoords().getX() + zone.getRadius()) / 16;
        int minChunkZ = (zone.getCoords().getZ() - zone.getRadius()) / 16;
        int maxChunkZ = (zone.getCoords().getZ() + zone.getRadius()) / 16;

        try {
            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    GlobalCoord coords = new GlobalCoord(zone.getCoords().getWorld(), x, 0, z);
                    Set<RuneZone> zones = this.zones.get(coords);
                    zones.remove(zone);
                    if (zones.isEmpty()) this.zones.invalidate(coords);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public @NotNull Optional<RuneZone> getZoneWithPosition(@NotNull GlobalCoord coords) {
        GlobalCoord asChunk = new GlobalCoord(coords.getWorld(), coords.getX() / 16, 0, coords.getZ() / 16);
        Set<RuneZone> zones = Collections.emptySet();
        try {
            zones = this.zones.get(asChunk);
            if (zones.isEmpty()) this.zones.invalidate(asChunk);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return zones.parallelStream().filter(zone -> zone.contains(coords)).findAny();
    }

    public boolean isCoordMaybePartOfRune(@NotNull GlobalCoord coords) {
        return getZoneWithPosition(coords).isPresent();
    }
}
