package net.boreeas.lively.util;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class Vec3Int {
    public final int x, y, z;

    public Vec3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3Int(@NotNull ByteBuf byteBuf) {
        this.x = byteBuf.readInt();
        this.y = byteBuf.readInt();
        this.z = byteBuf.readInt();
    }

    public @NotNull Vec3Int dx(int dx) {
        return new Vec3Int(x + dx, y, z);
    }

    public @NotNull Vec3Int dy(int dy) {
        return new Vec3Int(x, y + dy, z);
    }

    public @NotNull Vec3Int dz(int dz) {
        return new Vec3Int(x, y, z + dz);
    }

    public @NotNull Vec3Int add(int x, int y, int z) {
        return new Vec3Int(this.x + x, this.y + y, this.z + z);
    }

    public @NotNull Vec3Int add(@NotNull Vec3Int other) {
        return new Vec3Int(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    @Override
    public String toString() {
        return "Vec3Int[x=" + x + ",y=" + y + ",z=" + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vec3Int vec3Int = (Vec3Int) o;

        if (x != vec3Int.x) return false;
        if (y != vec3Int.y) return false;
        return z == vec3Int.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    public void serialize(ByteBuf byteBuf) {
        byteBuf.writeInt(x);
        byteBuf.writeInt(y);
        byteBuf.writeInt(z);
    }
}
