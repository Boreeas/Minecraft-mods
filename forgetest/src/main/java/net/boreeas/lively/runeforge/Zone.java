package net.boreeas.lively.runeforge;

import net.boreeas.lively.util.GlobalCoord;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class Zone {
    private GlobalCoord coords;
    private int radius;

    public Zone(@NotNull GlobalCoord coords, int radius) {
        this.coords = coords;
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public @NotNull GlobalCoord getCoords() {
        return coords;
    }

    public boolean contains(@NotNull GlobalCoord coords) {
        if (coords.getWorld() != this.coords.getWorld() || coords.getY() != this.coords.getY()) {
            return false;
        }

        int dx = coords.getX() - this.coords.getX();
        int dz = coords.getZ() - this.coords.getZ();

        return dx*dx + dz*dz <= radius*radius;
    }
}
