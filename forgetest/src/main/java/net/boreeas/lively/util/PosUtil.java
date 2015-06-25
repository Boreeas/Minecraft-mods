package net.boreeas.lively.util;

/**
 * @author Malte Schütze
 */
public class PosUtil {

    public static final int FACE_DOWN = 0;
    public static final int FACE_UP = 1;

    /**
     * Get the adjacent block by the block face
     * @param x x position
     * @param y y position
     * @param z z position
     * @param face block face
     * @return the position of the adjacent block
     */
    public static Vec3Int getAdjustedPosition(int x, int y, int z, int face) {
        if (face == FACE_DOWN) y--;
        if (face == FACE_UP) y++;
        if (face == 2) z--;
        if (face == 3) z++;
        if (face == 4) x--;
        if (face == 5) x++;

        return new Vec3Int(x, y, z);
    }
}
