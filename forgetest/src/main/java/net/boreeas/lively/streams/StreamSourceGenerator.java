package net.boreeas.lively.streams;

import cpw.mods.fml.common.IWorldGenerator;
import net.boreeas.lively.Lively;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.BiomeDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Malte Schütze
 */
public class StreamSourceGenerator implements IWorldGenerator {

    private static final Set<BiomeDictionary.Type> blacklistedBiomes = new HashSet<>(Arrays.asList(
            BiomeDictionary.Type.BEACH,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.RIVER,
            BiomeDictionary.Type.OCEAN
    ));

    private static final Set<BiomeDictionary.Type> forcedBiomes = new HashSet<>(Arrays.asList(
            BiomeDictionary.Type.MOUNTAIN
    ));
    public static final int MIN_HEIGHT = 100;


    @Override
    public void generate(@NotNull Random random, int chunkX, int chunkZ, @NotNull World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        long time = System.currentTimeMillis();

        int attempts = 50;
        int maxPlaced = 10;

        for (int i = 0; i < attempts && maxPlaced > 0; i--) {
            int xOffset = random.nextInt(16);
            int zOffset = random.nextInt(16);
            int y = 256;
            while (y > 0 && world.getBlock(xOffset, y--, zOffset) == Blocks.air) {}

            if (generate(world, random, chunkX * 16 + xOffset, y, chunkZ * 16 + zOffset)) {
                maxPlaced--;
            }
        }

        long delta = System.currentTimeMillis() - time;
        //Lively.INSTANCE.logger.info("Worldgen at (" + chunkX + ", " + chunkZ + ") took " + delta + " ms");
        if (delta > 10) {
            Lively.INSTANCE.logger.warn("Excessive worldgen at (" + chunkX + ", " + chunkZ + "): took " + delta + " ms");
        }
    }

    public boolean generate(@NotNull World world, Random random, int x, int y, int z) {
        //if (isSuitableBiome(world, x, y, z) && canGenerateAtPos(world, x, y, z)) {
            world.setBlock(x, y, z, Lively.BLOCK_STREAM_SOURCE);
            return true;
        //}

        //return false;
    }

    public boolean isSuitableBiome(@NotNull World world, int x, int y, int z) {
        BiomeGenBase biomeBase = world.getBiomeGenForCoords(x, z);

        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(biomeBase);
        for (BiomeDictionary.Type type: types) {
            if (forcedBiomes.contains(type)) {
                return true;
            }

            if (blacklistedBiomes.contains(type)) {
                return false;
            }
        }

        return true;
    }

    public boolean canGenerateAtPos(@NotNull World world, int x, int y, int z) {
        if (y < MIN_HEIGHT) return false;

        Block block = world.getBlock(x, y, z);
        return block == Blocks.stone || block == Blocks.dirt || block == Blocks.grass || block == Blocks.gravel;
    }

}
