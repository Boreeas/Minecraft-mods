package net.boreeas.lively.util;

/**
 * @author Malte Schütze
 */
public class Vec3Int {
    public int x, y, z;

    public Vec3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vec3Int[x=" + x + ",y=" + y + ",z=" + z + "]";
    }
}
