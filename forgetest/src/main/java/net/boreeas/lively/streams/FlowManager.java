package net.boreeas.lively.streams;

import net.boreeas.lively.util.GlobalCoord;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Malte Schütze
 */
public class FlowManager {
    @NotNull private final Map<GlobalCoord, PoolLayer> layers = new HashMap<>();

    public PoolLayer getLayerAt(@NotNull World world, int x, int y, int z) {
        return layers.get(new GlobalCoord(world, x, y, z));
    }

    public void addPoolLayer(@NotNull World world, int x, int y, int z, @NotNull PoolLayer layer) {
        layers.put(new GlobalCoord(world, x, y, z), layer);
    }

    public void mergeLayers(@NotNull PoolLayer first, @NotNull PoolLayer second) {
        for (StreamFlowTileEntity tile: second.getTiles()) {
            first.addTile(tile);
        }
    }
}
