package net.boreeas.lively.streams;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class PoolLayer {
    private int stored;
    @NotNull private final Set<StreamFlowTileEntity> tiles = new HashSet<>();

    public int getStored() {
        return stored;
    }

    public int getPerBlockAmount() {
        return stored / tiles.size();
    }

    public int getSize() {
        return tiles.size();
    }

    public void addTile(@Nonnull StreamFlowTileEntity tile) {
        this.tiles.add(tile);
        //tile.setAssociatedLayer(this);
    }

    public void removeTile(@Nonnull StreamFlowTileEntity tile) {
        this.tiles.remove(tile);
        //tile.setAssociatedLayer(null);
    }

    @NotNull
    public Set<StreamFlowTileEntity> getTiles() {
        return Collections.unmodifiableSet(tiles);
    }
}
