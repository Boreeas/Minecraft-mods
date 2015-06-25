package net.boreeas.lively.runeforge;

import net.boreeas.lively.Lively;
import net.boreeas.lively.util.PosUtil;
import net.boreeas.lively.util.Vec3Int;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Malte Schütze
 */
public class OuterRuneItem extends Item {
    public static final OuterRuneItem INSTANCE = new OuterRuneItem();
    public static final String NAME = "rune_item";

    public OuterRuneItem() {
        setCreativeTab(Lively.CREATIVE_TAB_RUNEFORGE);
        setTextureName(Lively.muid(NAME));
        setUnlocalizedName(Lively.muid(NAME));
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int face, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        Vec3Int pos = PosUtil.getAdjustedPosition(x, y, z, face);
        x = pos.x;
        y = pos.y;
        z = pos.z;

        if (world.getBlock(x, y, z) != Blocks.air || !player.canPlayerEdit(x, y, z, face, stack)) {
            return false;
        }

        if (RunicLine.INSTANCE.canPlaceBlockAt(world, x, y, z)) {
            stack.stackSize--;
            world.setBlock(x, y, z, RunicLine.INSTANCE);
            return true;
        }

        return false;
    }
}
