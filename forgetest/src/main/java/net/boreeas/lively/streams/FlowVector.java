package net.boreeas.lively.streams;

/**
 * @author Malte Schütze
 */
public class FlowVector {
    public final Direction direction;
    public int amt;

    public FlowVector(Direction direction, int amt) {
        this.direction = direction;
        this.amt = amt;
    }

    public String toString() {
        return "Flow[" + direction + " / " + amt + "]";
    }
}
