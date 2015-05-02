package net.boreeas.lively;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.boreeas.lively.streams.StreamBlock;
import net.boreeas.lively.streams.StreamFlowTileEntity;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.Logger;

/**
 * @author Malte Schütze
 */
@Mod(modid = Lively.MOD_ID, name = "Lively", version = Lively.VERSION)
public class Lively {
    public static final String FLUID_STREAM_WATER_NAME = "lively:streamwater";
    public static final String VERSION = "0.0.1";
    public static final String MOD_ID = "lively";

    @Mod.Instance(MOD_ID)
    public static Lively INSTANCE;

    public Logger logger;

    public static final Fluid FLUID_STREAM_WATER = new Fluid(FLUID_STREAM_WATER_NAME);
    public static final StreamBlock BLOCK_STREAM_SOURCE = new StreamBlock(FLUID_STREAM_WATER, Material.water);

    static {
        FLUID_STREAM_WATER.setBlock(BLOCK_STREAM_SOURCE);
    }

    @Mod.EventHandler
    public void preload(FMLPreInitializationEvent evt) {
        INSTANCE = this;
        this.logger = evt.getModLog();
        logger.info("[Lively] Entering preload");

        FluidRegistry.registerFluid(FLUID_STREAM_WATER);
        GameRegistry.registerBlock(BLOCK_STREAM_SOURCE, StreamBlock.NAME);
        GameRegistry.registerTileEntity(StreamFlowTileEntity.class, StreamFlowTileEntity.NAME);
        GameRegistry.addShapelessRecipe(new ItemStack(BLOCK_STREAM_SOURCE, 4), new ItemStack(Blocks.dirt));

        //GameRegistry.registerWorldGenerator(new StreamSourceGenerator(), 100);
    }

}
