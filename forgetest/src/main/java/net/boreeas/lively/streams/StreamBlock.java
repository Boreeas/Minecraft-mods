package net.boreeas.lively.streams;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * @author Malte Schütze
 */
public class StreamBlock extends BlockFluidBase implements ITileEntityProvider {
    private static final int FLAG_UPDATE_CLIENTS = 2;
    @NotNull public static final String NAME = "blockStreamSource";

    public StreamBlock(@NotNull Fluid fluid, @NotNull Material material) {
        super(fluid, material);
        super.setDensity(500);
    }

    @Override
    public boolean canDisplace(@NotNull IBlockAccess world, int x, int y, int z) {
        return world.getBlock(x, y, z) != Blocks.water && super.canDisplace(world, x, y, z);
    }

    @Override
    public int getQuantaValue(@NotNull IBlockAccess world, int x, int y, int z) {
        if (world.getBlock(x, y, z) == Blocks.air) {
            return 0;
        }

        if (world.getBlock(x, y, z) != this) {
            return -1;
        }

        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public boolean canCollideCheck(int meta, boolean fullHit) {
        return fullHit;
    }

    @Override
    public int getMaxRenderHeightMeta() {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(@NotNull World world, int x, int y, int z, boolean doDrain) {
        world.setBlockMetadataWithNotify(x, y, z, 0, FLAG_UPDATE_CLIENTS);
        return null;
    }

    @Override
    public boolean canDrain(@NotNull World world, int x, int y, int z) {
        return world.getBlock(x, y, z) == this;
    }

    @Override
    public void updateTick(@NotNull World world, int x, int y, int z, Random rand) {
    }

    @NotNull
    @Override
    public TileEntity createNewTileEntity(@NotNull World p_149915_1_, int p_149915_2_) {
        return new StreamFlowTileEntity();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }
}
