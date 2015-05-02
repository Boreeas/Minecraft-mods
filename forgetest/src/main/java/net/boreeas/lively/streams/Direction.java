package net.boreeas.lively.streams;

/**
 * @author Malte Schütze
 */
public enum Direction {
    NORTH(1, 0, 0),
    SOUTH(-1, 0, 0),
    WEST(0, 0, -1),
    EAST(0, 0, 1),
    DOWN(0, -1, 0),
    UP(0, 1, 0);


    public final int dx;
    public final int dy;
    public final int dz;

    private Direction(int dx, int dy, int dz) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
    }
}
