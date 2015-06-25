package net.boreeas.lively.streams;

import net.boreeas.lively.Lively;
import net.boreeas.lively.util.Direction;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Rule-based system for distributing finite water. The following rules are applied in order:
 *
 * <ol>
 *     <li>
 *         If the block below has space for water, send as much water as possible. If the depth of the lower block
 *         is equal to or lower than the current depth, set the depth of the lower block to current depth + 1.
 *     </li>
 *     <li>
 *         If any of the adjacent four blocks has less water than this block, average the water level in both blocks.
 *         If the depth of the adjacent block is lower than the current depth, set the depth of the adjacent block
 *         to current depth.
 *     </li>
 *     <li>
 *         If the current depth is greater or equal to 1, and the block above has space for water, and the current block
 *         if full, send a fraction of water upwards. If the depth of the upper block is lower than current depth - 1,
 *         set the depth of the upper block to current depth - 1.
 *     </li>
 *     <li>
 *         If none of the above rules applied, evaporate a fraction of water in the current block.
 *     </li>
 * </ol>
 *
 * @author Malte Schütze
 */
public class StreamFlowTileEntity extends TileEntity {
    public static final String NAME = "tileEntityStreamFlow";
    /**
     * Optimal pressure at this amount
     */
    public static final int NOMINAL_WATER = 1000;
    /**
     * Maximum water in a block in mB
     */
    public static final int MAX_WATER = NOMINAL_WATER * 2;
    private static final int ROUNDS = 5;
    private static final int MAX_PASSIVE_UPDATES = 3;
    /**
     * Blocks that can be removed when trying to flow there
     */
    public static final Set<Block> displacableBlocks = new HashSet<>(Arrays.asList(
            Blocks.air,
            Blocks.red_flower,
            Blocks.yellow_flower,
            Blocks.tallgrass,
            Blocks.snow_layer,
            Blocks.flower_pot,
            Blocks.brown_mushroom,
            Blocks.cake,
            Blocks.carrots,
            Blocks.cocoa,
            Blocks.deadbush,
            Blocks.double_plant,
            Blocks.melon_stem,
            Blocks.nether_wart,
            Blocks.pumpkin_stem,
            Blocks.potatoes,
            Blocks.rail,
            Blocks.red_flower,
            Blocks.red_mushroom,
            Blocks.redstone_torch,
            Blocks.redstone_wire,
            Blocks.reeds,
            Blocks.unlit_redstone_torch,
            Blocks.sapling,
            Blocks.torch,
            Blocks.vine,
            Blocks.web,
            Blocks.wheat
    ));
    /**
     * Blocks that absorb incoming water
     */
    public static final Set<Block> absorbingBlocks = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            Blocks.water,
            Blocks.flowing_water,
            Blocks.sponge
    )));

    private static final long UPDATE_TIME_TICKS = 5;
    private static final int LOSS = (int) (0.02 * MAX_WATER); // 2%

    private int waterInBlock = 1000;
    private boolean isInfinite = false;
    private int depth = 0;
    @NotNull
    private final Set<StreamFlowTileEntity> parents = new HashSet<>();
    private int passiveUpdates;

    @Override
    public void updateEntity() {
        if (worldObj.getTotalWorldTime() % UPDATE_TIME_TICKS != 0) return;

        if (canFlowDown()) {
            flowDown();
        }

        Set<Direction> possibleLateralDirections = getPossibleLateralDirections();
        if (!possibleLateralDirections.isEmpty()) {
            flowLaterally(possibleLateralDirections);
        }

        if (canFlowUp()) {
            flowUp();
        }


        if (waterInBlock < NOMINAL_WATER) {
            addWaterInBlock(request(NOMINAL_WATER - waterInBlock));
        }


        if (passiveUpdates++ >= MAX_PASSIVE_UPDATES) {
            addWaterInBlock(-LOSS);
        }


        checkForDry();
    }

    private void checkForDry() {
        if (waterInBlock == 0) {
            worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
            for (Direction direction: Direction.values()) {
                getAdjStreamFlow(direction).onNeighborDriedOut(this);
            }
        }
    }

    private void onNeighborDriedOut(StreamFlowTileEntity flow) {
        parents.remove(flow);
    }

    public int request(int amt) {
        return request(amt, new HashSet<>());
    }

    private int request(int amt, @NotNull HashSet<StreamFlowTileEntity> blacklist) {
        passiveUpdates = 0;
        blacklist.add(this);

        int accumulated = 0;
        int polls = 0;
        Set<StreamFlowTileEntity> open = new HashSet<>(parents);
        while (accumulated < amt && polls++ < ROUNDS && !open.isEmpty()) {
            Set<StreamFlowTileEntity> toRemove = new HashSet<>();

            int reqAmt = Math.max((amt - accumulated) / open.size(), (int) (MAX_WATER * 0.01));

            for (StreamFlowTileEntity parent: open) {
                if (blacklist.contains(parent)) {
                    toRemove.add(parent);
                    continue;
                }

                int received = parent.request(reqAmt, blacklist);

                // Streams that can't fulfill requests are not polled again
                if (received < reqAmt) {
                    toRemove.add(parent);
                }

                addWaterInBlock(received);
            }

            open.removeAll(toRemove);
        }

        int missing = Math.min(amt - accumulated, waterInBlock);
        addWaterInBlock(-missing);

        return accumulated + missing;
    }


    @NotNull
    private Direction randomDirection() {
        switch (worldObj.rand.nextInt(4)) {
            case 0: return Direction.NORTH;
            case 1: return Direction.SOUTH;
            case 2: return Direction.EAST;
            case 3:
            default: return  Direction.WEST;
        }
    }

    private void flowUp() {
        Block target = getBlock(Direction.UP);
        StreamFlowTileEntity targetFlow = getAdjStreamFlow(Direction.UP);
        int x = this.xCoord;
        int y = this.yCoord + 1;
        int z = this.zCoord;

        if (displacableBlocks.contains(target)) {

            displace(x, y, z, getWaterInBlock() - (NOMINAL_WATER / 2));
        } /*else if (target == Lively.BLOCK_STREAM_SOURCE) {

            int toPush = Math.min(getWaterInBlock() - NOMINAL_WATER, MAX_WATER - targetFlow.getWaterInBlock());
            targetFlow.addWaterInBlock(toPush);
            this.addWaterInBlock(-toPush);
        } */ else {

            Lively.INSTANCE.logger.warn("Couldn't flow upwards to block " + target.getUnlocalizedName() + " at (" + x + ", " + y + ", " + z + ")");
        }

        targetFlow.depth = Math.max(depth - 1, targetFlow.depth);
        targetFlow.addParent(this);
    }

    private void flowLaterally(@NotNull Set<Direction> directions) {

        int sum = getWaterInBlock();
        int maxDepth = depth;

        for (Direction direction: directions) {
            Block target = getBlock(direction);
            int x = this.xCoord + direction.dx;
            int y = this.yCoord;
            int z = this.zCoord + direction.dz;

            if (displacableBlocks.contains(target)) {
                displace(x, y, z, 0);
            } /*else {
                StreamFlowTileEntity flow = getAdjStreamFlow(direction);

                sum += flow.getWaterInBlock();
                if (flow.depth > maxDepth) {
                    maxDepth = flow.depth;
                }
            }*/
        }

        int average = sum / (directions.size() + 1);
        for (Direction direction: directions) {
            getAdjStreamFlow(direction).setWaterInBlock(average);
            getAdjStreamFlow(direction).depth = maxDepth;
            getAdjStreamFlow(direction).addParent(this);
        }

        setWaterInBlock(average);
        depth = maxDepth;
    }

    private void flowDown() {
        Block target = getBlock(Direction.DOWN);
        StreamFlowTileEntity targetFlow = getAdjStreamFlow(Direction.DOWN);
        int x = this.xCoord;
        int y = this.yCoord - 1;
        int z = this.zCoord;

        if (displacableBlocks.contains(target)) {

            displace(x, y, z, getWaterInBlock());
        } else if (absorbingBlocks.contains(target)) {

            this.setWaterInBlock(0);
        } /* else if (target == Lively.BLOCK_STREAM_SOURCE) {

            int toPush = Math.min(this.getWaterInBlock(), NOMINAL_WATER - targetFlow.getWaterInBlock());
            targetFlow.addWaterInBlock(toPush);
            this.addWaterInBlock(-toPush);
        } */ else {

            Lively.INSTANCE.logger.warn("Couldn't flow down to block " + target.getUnlocalizedName() + " at (" + x + ", " + y + ", " + z + ")");
        }


        targetFlow.depth = Math.max(depth + 1, targetFlow.depth);
        targetFlow.addParent(this);
    }

    private void addParent(StreamFlowTileEntity streamFlowTileEntity) {
        this.parents.add(streamFlowTileEntity);
    }

    private void displace(int x, int y, int z, int amount) {
        for (ItemStack itemStack: worldObj.getBlock(x, y, z).getDrops(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0)) {
            EntityItem item = new EntityItem(worldObj, x, y, z, itemStack);
            worldObj.spawnEntityInWorld(item);
        }

        worldObj.setBlock(x, y, z, Lively.BLOCK_STREAM_SOURCE);
        getFlow(x, y, z).setWaterInBlock(amount);
        if (!isInfinite()) {
            this.setWaterInBlock(-amount);
        }
    }

    private boolean canFlowDown() {
        Block target = getBlock(Direction.DOWN);
        //return (target == Lively.BLOCK_STREAM_SOURCE && getFlow(Direction.DOWN).getWaterInBlock() < NOMINAL_WATER)
        return displacableBlocks.contains(target) || absorbingBlocks.contains(target);
    }

    @NotNull
    private Set<Direction> getPossibleLateralDirections() {
        Set<Direction> result = new HashSet<>();
        if (canFlowLaterally(Direction.NORTH)) result.add(Direction.NORTH);
        if (canFlowLaterally(Direction.SOUTH)) result.add(Direction.SOUTH);
        if (canFlowLaterally(Direction.WEST)) result.add(Direction.WEST);
        if (canFlowLaterally(Direction.EAST)) result.add(Direction.EAST);

        return result;
    }

    private boolean canFlowLaterally(@NotNull Direction adjacent) {
        Block target = getBlock(adjacent);
        // return (target == Lively.BLOCK_STREAM_SOURCE && getFlow(adjacent).getWaterInBlock() < this.getWaterInBlock())
        if (canFlowDown() || hasAdjFlowRoom(Direction.DOWN)) return false;
        return displacableBlocks.contains(target) /*|| absorbingBlocks.contains(target)*/;
    }

    private boolean hasAdjFlowRoom(@NotNull Direction direction) {
        return getBlock(direction) == Lively.BLOCK_STREAM_SOURCE && getAdjStreamFlow(direction).getWaterInBlock() < NOMINAL_WATER;
    }

    private boolean canFlowUp() {
        Block target = getBlock(Direction.UP);
        if (canFlowDown()) return false;
        if (!getPossibleLateralDirections().isEmpty()) return false;
        if (hasAdjFlowRoom(Direction.NORTH) || hasAdjFlowRoom(Direction.SOUTH) || hasAdjFlowRoom(Direction.EAST) || hasAdjFlowRoom(Direction.WEST)) {
            return false;
        }
        if (depth == 0 || waterInBlock <= NOMINAL_WATER / 2) return false;


        //return (target == Lively.BLOCK_STREAM_SOURCE && getFlow(Direction.UP).getWaterInBlock() < MAX_WATER)
        return displacableBlocks.contains(target);
    }

    public int getWaterInBlock() {
        return waterInBlock;
    }

    public void addWaterInBlock(int amt) {
        this.waterInBlock += amt;
        if (waterInBlock > MAX_WATER) waterInBlock = MAX_WATER;
        if (waterInBlock < 0) waterInBlock = 0;
    }

    public void setWaterInBlock(int amt) {
        this.waterInBlock = amt;
        if (waterInBlock > MAX_WATER) waterInBlock = MAX_WATER;
        if (waterInBlock < 0) waterInBlock = 0;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setInfinite(boolean flag) {
        this.isInfinite = flag;
    }


    private Block getBlock(@NotNull Direction direction) {
        return worldObj.getBlock(xCoord + direction.dx, yCoord + direction.dy, zCoord + direction.dz);
    }

    @NotNull
    private StreamFlowTileEntity getAdjStreamFlow(@NotNull Direction direction) {
        return getFlow(xCoord + direction.dx, yCoord + direction.dy, zCoord + direction.dz);
    }

    @NotNull
    private StreamFlowTileEntity getFlow(int x, int y, int z) {
        Object flow = worldObj.getTileEntity(x, y, z);
        if (flow == null || !(flow instanceof StreamFlowTileEntity)) {
            // Sometimes blocks get removed by race conditions or similar
            return DummyStreamFlowTileEntity.INSTANCE;
        }

        return (StreamFlowTileEntity) flow;
    }

    public int getDepth() {
        return depth;
    }

    private static class DummyStreamFlowTileEntity extends StreamFlowTileEntity {
        @NotNull
        static final DummyStreamFlowTileEntity INSTANCE = new DummyStreamFlowTileEntity();

        @Override
        public int getWaterInBlock() {
            return 0;
        }

        @Override
        public void addWaterInBlock(int amt) {
        }

        @Override
        public void setWaterInBlock(int amt) {
        }

        @Override
        public void updateEntity() {
        }

        @Override
        public boolean isInfinite() {
            return false;
        }

        @Override
        public int getDepth() {
            return 0;
        }

        @Override
        public int request(int amt) {
            return 0;
        }

        @Override
        public void setInfinite(boolean flag) {
        }
    }
}
