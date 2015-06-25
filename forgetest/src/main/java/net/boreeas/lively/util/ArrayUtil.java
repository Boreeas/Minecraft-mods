package net.boreeas.lively.util;

import net.boreeas.lively.Lively;

/**
 * @author Malte Schütze
 */
public class ArrayUtil {
    public static boolean[][] trim(boolean[][] original) {
        int minX = -1;
        int maxX = 0;
        int minZ = -1;
        int maxZ = 0;

        for (int z = 0; z < original.length; z++) {
            for (int x = 0; x < original[z].length; x++) {
                if (original[z][x]) {
                    if (x < minX) {
                        minX = x;
                    }
                    if (x > minX && minX == -1) {
                        minX = x;
                    }
                    if (x > maxX) {
                        maxX = x;
                    }

                    if (z < minZ) minZ = z;
                    if (z > minZ && minZ == -1) minZ = z;
                    if (z > maxZ) maxZ = z;
                }
            }
        }

        if (minX == -1 || minZ == -1) {
            // Empty array
            return new boolean[0][0];
        }


        int newZ = maxZ - minZ + 1;
        int newX = maxX - minX + 1;
        boolean[][] copy = new boolean[newZ][newX];

        for (int z = 0; z < copy.length; z++) {
            for (int x = 0; x < copy[0].length; x++) {
                copy[z][x] = original[minZ + z][minX + x];
            }
        }

        return copy;
    }

    public static void dump(boolean[][] flags) {
        for (boolean[] row : flags) {
            StringBuilder builder = new StringBuilder();
            for (boolean flag : row) {
                builder.append("[" + (flag ? 'X' : ' ') + "]");
            }

            Lively.INSTANCE.logger.info(builder);
        }
    }
}
