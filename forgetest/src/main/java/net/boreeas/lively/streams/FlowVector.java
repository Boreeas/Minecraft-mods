package net.boreeas.lively.streams;

import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
public class FlowVector {
    @NotNull
    public final Direction direction;
    public final int amt;

    public FlowVector(@NotNull Direction direction, int amt) {
        this.direction = direction;
        this.amt = amt;
    }

    @NotNull
    public String toString() {
        return "Flow[" + direction + " / " + amt + "]";
    }
}
