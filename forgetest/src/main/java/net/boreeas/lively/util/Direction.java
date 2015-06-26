package net.boreeas.lively.util;

/**
 * @author Malte Schütze
 */
public enum Direction {
    /**
     * dx=0, dz=-1
     */
    NORTH(0, 0, -1),
    /**
     * dx=0, dx=1
     */
    SOUTH(0, 0, 1),
    /**
     * dx=-1, dz=0
     */
    WEST(-1, 0, 0),
    /**
     * dx=1, dz=0
     */
    EAST(1, 0, 0),
    /**
     * dy=-1
     */
    DOWN(0, -1, 0),
    /**
     * dy=1
     */
    UP(0, 1, 0);


    public final int dx;
    public final int dy;
    public final int dz;

    Direction(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }

    public Direction invert() {
        switch (this) {
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case WEST: return EAST;
            case EAST: return WEST;
            case DOWN: return UP;
            default:
            case UP: return DOWN;
        }
    }
}
