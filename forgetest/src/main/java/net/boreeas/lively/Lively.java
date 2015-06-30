package net.boreeas.lively;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.boreeas.lively.runeforge.*;
import net.boreeas.lively.runeforge.runes.RuneContain;
import net.boreeas.lively.runeforge.runes.RuneHealth;
import net.boreeas.lively.runeforge.runes.RuneNot;
import net.boreeas.lively.streams.StreamBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Malte Schütze
 */
@Mod(modid = Lively.MOD_ID, name = "Lively", version = Lively.VERSION)
public class Lively {
    public static final String FLUID_STREAM_WATER_NAME = "lively:streamwater";
    public static final String VERSION = "0.0.1";
    public static final String MOD_ID = "lively";
    public static final CreativeTabs CREATIVE_TAB_RUNEFORGE = new CreativeTabs("Runeforge") {
        @Override
        public Item getTabIconItem() {
            return OuterRuneItem.INSTANCE;
        }
    };

    @Mod.Instance(MOD_ID)
    public static Lively INSTANCE;

    public Logger logger;
    public final RuneRegistry runeRegistry = new RuneRegistry();
    public final EffectZoneLookup effectZoneLookup = new EffectZoneLookup();
    public final RuneZoneLookup runeZoneLookup = new RuneZoneLookup();

    public static final Fluid FLUID_STREAM_WATER = new Fluid(FLUID_STREAM_WATER_NAME);
    public static final StreamBlock BLOCK_STREAM_SOURCE = new StreamBlock(FLUID_STREAM_WATER, Material.water);

    static {
        FLUID_STREAM_WATER.setBlock(BLOCK_STREAM_SOURCE);
    }

    @Mod.EventHandler
    public void preload(@NotNull FMLPreInitializationEvent evt) {
        INSTANCE = this;
        this.logger = evt.getModLog();
        logger.info("[Lively] Entering preload");

        /*
        FluidRegistry.registerFluid(FLUID_STREAM_WATER);
        GameRegistry.registerBlock(BLOCK_STREAM_SOURCE, StreamBlock.NAME);
        GameRegistry.registerTileEntity(StreamFlowTileEntity.class, StreamFlowTileEntity.NAME);
        GameRegistry.addShapelessRecipe(new ItemStack(BLOCK_STREAM_SOURCE, 4), new ItemStack(Blocks.dirt));
        */

        //GameRegistry.registerWorldGenerator(new StreamSourceGenerator(), 100);

        GameRegistry.registerBlock(RunicLine.INSTANCE, RunicLine.NAME);
        GameRegistry.registerItem(OuterRuneItem.INSTANCE, OuterRuneItem.NAME);

        runeRegistry.register(new RuneHealth());
        runeRegistry.register(new RuneNot());
        runeRegistry.register(new RuneContain());

        MinecraftForge.EVENT_BUS.register(RunicLine.INSTANCE);
        FMLCommonHandler.instance().bus().register(effectZoneLookup);
    }

    public static String muid(String name) {
        return MOD_ID + ":" + name;
    }

    public static String muid2(String name) {
        return MOD_ID + "_" + name;
    }
}
