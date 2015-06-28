package net.boreeas.lively.runeforge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.boreeas.lively.Lively;
import net.boreeas.lively.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * @author Malte Schütze
 */
public class RunicLine extends Block {
    public static final RunicLine INSTANCE = new RunicLine();
    public static final String NAME = "rune_outer";
    public static final Map<Block, Integer> containmentRatings = new HashMap<>();
    static {
        containmentRatings.put(Blocks.planks, 1);
        containmentRatings.put(Blocks.stone, 2);
        containmentRatings.put(Blocks.lapis_block, 5);
        containmentRatings.put(Blocks.diamond_block, 5);
    }
    public static final Map<Block, Integer> focusRatings = new HashMap<>();
    static {
        focusRatings.put(Blocks.lapis_block, 2);
        focusRatings.put(Blocks.diamond_block, 5);
    }

    private static final int MAX_FOCUS_RADIUS = 8;
    private IIcon iconBlank;
    private IIcon iconSingle;
    private IIcon iconIntersection;
    private IIcon iconConnector1;
    private IIcon iconConnector2;
    private IIcon[] iconsEndpoint = new IIcon[4];
    private IIcon[] iconsTIntersection = new IIcon[4];
    private IIcon[] iconsCorner = new IIcon[4];

    protected RunicLine() {
        super(Material.carpet);
        setBlockBounds(0, 0, 0, 1, 0.0125f, 1);
        setBlockName(Lively.muid(NAME));
        setBlockTextureName(Lively.muid(NAME));
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBoxFromPool(@NotNull World world, int x, int y, int z) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(@NotNull World world, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(world, x, y - 1, z);
    }

    @Override
    @NotNull
    public Item getItemDropped(int a, @NotNull Random rand, int b) {
        return OuterRuneItem.INSTANCE;
    }

    @Override
    public void registerBlockIcons(@NotNull IIconRegister register) {
        this.iconSingle = register.registerIcon(Lively.muid("rune_single"));
        this.iconIntersection = register.registerIcon(Lively.muid("rune_intersection"));
        this.iconConnector1 = register.registerIcon(Lively.muid("rune_connector"));
        this.iconConnector2 = register.registerIcon(Lively.muid("rune_connector2"));
        this.iconBlank = register.registerIcon(Lively.muid("blank"));

        for (int i = 1; i <= 4; i++) {
            this.iconsEndpoint[i - 1] = register.registerIcon(Lively.muid("rune_endpoint" + i));
            this.iconsTIntersection[i - 1] = register.registerIcon(Lively.muid("rune_tintersect" + i));
            this.iconsCorner[i - 1] = register.registerIcon(Lively.muid("rune_corner" + i));
        }
    }

    @Override
    @NotNull
    public IIcon getIcon(@NotNull IBlockAccess iba, int x, int y, int z, int face) {
        if (face != PosUtil.FACE_UP) return iconBlank;

        boolean top = iba.getBlock(x, y, z - 1) == this;
        boolean down = iba.getBlock(x, y, z + 1) == this;
        boolean left = iba.getBlock(x - 1, y, z) == this;
        boolean right = iba.getBlock(x + 1, y, z) == this;

        int sum = (top ? 1 : 0) + (down ? 1 : 0) + (left ? 1 : 0) + (right ? 1 : 0);

        switch (sum) {
            case 0:
                return iconSingle;
            case 1:
                if (top) return iconsEndpoint[3];
                if (right) return iconsEndpoint[0];
                if (down) return iconsEndpoint[1];
                return iconsEndpoint[2];
            case 2:
                if (top) {
                    if (left) return iconsCorner[2];
                    if (down) return iconConnector2;
                    return iconsCorner[3];
                }

                if (right) {
                    if (down) return iconsCorner[0];
                    return iconConnector1;
                }

                return iconsCorner[1];
            case 3:
                if (top && right && down) return iconsTIntersection[3];
                if (right && down && left) return iconsTIntersection[0];
                if (top && left && down) return iconsTIntersection[1];
                return iconsTIntersection[2];
            default:
            case 4:
                return iconIntersection;
        }
    }

    @SubscribeEvent
    public void onRuneActivation(@NotNull PlayerInteractEvent evt) throws ExecutionException {
        World world = evt.world;
        int x = evt.x;
        int y = evt.y;
        int z = evt.z;

        if (world.isRemote) return;

        if (evt.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        // Allow sneaking to place blocks instead of trying to activate a rune
        if (evt.entityPlayer.isSneaking()) return;

        if (isFocusBlock(world.getBlock(x, y, z))) {
            if (Lively.INSTANCE.runeZoneLookup.isCoordMaybePartOfRune(new GlobalCoord(world, x, y + 1, z))) return; // No double activation
            evt.setCanceled(true);
            onFocusBlockClicked(world, x, y, z, evt.entityPlayer);
        } else if (world.getBlock(x, y, z) == this) {
            if (Lively.INSTANCE.runeZoneLookup.isCoordMaybePartOfRune(new GlobalCoord(world, x, y, z))) return; // No double activation
            evt.setCanceled(true);
            onRuneBlockClicked(world, x, y, z, evt.entityPlayer);
        }
    }

    private void onRuneBlockClicked(@NotNull World world, int x, int y, int z, @NotNull EntityPlayer player) throws ExecutionException {
        if (!isFocusBlock(world.getBlock(x, y - 1, z))) {
            player.addChatMessage(new ChatComponentText("You feel energy stirring, but it is lacking a focus. The energy dissipates."));
            return;
        }

        onFocusBlockClicked(world, x, y - 1, z, player);
    }

    private void onFocusBlockClicked(@NotNull World world, int x, int y, int z, @NotNull EntityPlayer player) throws ExecutionException {
        Optional<Vec3Int> alignmentPosOpt = findAlignmentBlock(x, y, z, world);
        if (!alignmentPosOpt.isPresent()) {
            player.addChatMessage(new ChatComponentText("The energy is focused through the block, but lacking a direction, is flows away."));
            return;
        }

        Vec3Int alignmentPos = alignmentPosOpt.get();
        int radius = x == alignmentPos.x ? Math.abs(z - alignmentPos.z) : Math.abs(x - alignmentPos.x);
        Result<Integer, Vec3Int> circle = matchCircle(world, new Vec3Int(x, y, z), radius);
        if (!circle.isOk()) {
            alertBrokenLink(circle.getFail(), player);
            return;
        }

        Direction alignment = alignmentPos.x == x ? (z > alignmentPos.z ? Direction.NORTH : Direction.SOUTH) : (x < alignmentPos.x ? Direction.EAST : Direction.WEST);
        boolean[][] runeBlocks = loadRune(world, x, y + 1, z, radius, alignment);

        Optional<Rune> match = Lively.INSTANCE.runeRegistry.match(runeBlocks);
        if (!match.isPresent()) {
            player.addChatMessage(new ChatComponentText("The energy circulates inside the rune. But nothing happens..."));
        } else {
            player.addChatMessage(new ChatComponentText("The energy circulates inside the rune. You feel a distant humming..."));
            activateRune(match.get(), new GlobalCoord(world, x, y + 1, z), radius, focusRatings.get(world.getBlock(x, y, z)), circle.getOk());
        }
    }

    private void alertBrokenLink(@NotNull Vec3Int brokenLink, EntityPlayer player) {
        player.addChatMessage(new ChatComponentText("The energy builds up, but lacks containment. The energy disappears"));
        String fmtX = brokenLink.x < 0 ? (-brokenLink.x + " blocks west") : (brokenLink.x + " blocks east");
        String fmtZ = brokenLink.z < 0 ? (-brokenLink.z + " blocks north") : (brokenLink.z + " blocks south");
        player.addChatMessage(new ChatComponentText("(Containment circle lacking component " + fmtX + ", " + fmtZ + ")"));
    }

    private void activateRune(@NotNull Rune match, @NotNull GlobalCoord coords, int radius, int focusRating, int containmentRating) throws ExecutionException {
        RuneZone zone = new RuneZone(coords, radius);

        int effectStrength = calculateEffectStrength(radius, focusRating, containmentRating);
        EffectZone effectZone = new EffectZone(coords, effectStrength, radius * Math.min(focusRating, containmentRating));
        Effect effect = match.makeEffect(zone, effectZone);
        zone.setAssociatedEffect(effect);
        effectZone.setAssociatedEffect(effect);

        checkForModificators(coords.getWorld(), coords.getX(), coords.getY() - 1, coords.getZ(), radius, effect);
        Lively.INSTANCE.effectZoneLookup.addEffectZone(effectZone);
        Lively.INSTANCE.runeZoneLookup.add(zone);
    }

    private void checkForModificators(World world, int x, int y, int z, int radius, Effect parent) {

        if (isValidContainmentBlock(world.getBlock(x, y, z)) && isValidContainmentBlock(world.getBlock(x, y, z))) {
            scanForModificatorRune(world, x + radius + 1, y, z, Direction.EAST, parent);
        }

        if (isValidContainmentBlock(world.getBlock(x, y, z)) && isValidContainmentBlock(world.getBlock(x, y, z))) {
            scanForModificatorRune(world, x - radius - 1, y, z, Direction.WEST, parent);
        }

        if (isValidContainmentBlock(world.getBlock(x, y, z)) && isValidContainmentBlock(world.getBlock(x, y, z))) {
            scanForModificatorRune(world, x, y, z + radius + 1, Direction.SOUTH, parent);
        }

        if (isValidContainmentBlock(world.getBlock(x, y, z)) && isValidContainmentBlock(world.getBlock(x, y, z))) {
            scanForModificatorRune(world, x, y, z - radius - 1, Direction.NORTH, parent);
        }
    }

    private void scanForModificatorRune(World world, int x, int y, int z, Direction direction, Effect parent) {
        Block block = world.getBlock(x, y, z);
        while (isValidContainmentBlock(block) && !isFocusBlock(block)) {
            x += direction.dx;
            z += direction.dz;
            block = world.getBlock(x, y, z);
        }

        if (!isFocusBlock(world.getBlock(x, y, z))) {
            return;
        }

        x += direction.dx;
        z += direction.dz;

        int radius;
        for (radius = 1; radius <= MAX_FOCUS_RADIUS; radius++) {
            if (isFocusBlock(world.getBlock(x, y, z))) {
                break;
            }

            x += direction.dx;
            z += direction.dz;
        }

        if (!isFocusBlock(world.getBlock(x, y, z))) {
            return;
        }

        Result<Integer, Vec3Int> circle = matchCircle(world, new Vec3Int(x, y, z), radius);
        if (!circle.isOk()) {
            return;
        }

        boolean[][] runeBlocks = loadRune(world, x, y + 1, z, radius, direction.invert());
        Optional<Rune> match = Lively.INSTANCE.runeRegistry.match(runeBlocks);

        if (!match.isPresent()) {
            return;
        }

        RuneZone zone = new RuneZone(new GlobalCoord(world, x, y + 1, z), radius);
        Effect effect = match.get().makeEffect(zone, parent);
        zone.setAssociatedEffect(effect);

        Lively.INSTANCE.runeZoneLookup.add(zone);
        checkForModificators(world, x, y, z, radius, effect);
    }

    /**
     * Checks whether a complete circle made out of containment blocks exists around the specified center position
     *
     * @param world  The world where the center is placed
     * @param center The center of the circle
     * @param radius The radius of the circle
     * @return Either a Vec3Int containing the bad position relative to the center, or the minimal containment rating of the circle
     */
    @NotNull
    private Result<Integer, Vec3Int> matchCircle(@NotNull World world, @NotNull Vec3Int center, int radius) {
        // See https://en.wikipedia.org/wiki/Midpoint_circle_algorithm#Example
        int x = radius;
        int z = 0;
        int flag = 1 - x;

        int minRating = Integer.MAX_VALUE;

        while (x >= z) { // welcome to typo central
            if (!isValidContainmentBlock(world.getBlock(center.x + x, center.y, center.z + z)))
                return Result.fail(new Vec3Int(x, 0, z));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x + x, center.y, center.z + z)));

            if (!isValidContainmentBlock(world.getBlock(center.x + z, center.y, center.z + x)))
                return Result.fail(new Vec3Int(z, 0, x));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x + z, center.y, center.z + x)));

            if (!isValidContainmentBlock(world.getBlock(center.x - x, center.y, center.z + z)))
                return Result.fail(new Vec3Int(-x, 0, z));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x - x, center.y, center.z + z)));

            if (!isValidContainmentBlock(world.getBlock(center.x - z, center.y, center.z + x)))
                return Result.fail(new Vec3Int(-z, 0, x));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x - z, center.y, center.z + x)));

            if (!isValidContainmentBlock(world.getBlock(center.x + x, center.y, center.z - z)))
                return Result.fail(new Vec3Int(x, 0, -z));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x + x, center.y, center.z - z)));

            if (!isValidContainmentBlock(world.getBlock(center.x + z, center.y, center.z - x)))
                return Result.fail(new Vec3Int(z, 0, -x));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x + z, center.y, center.z - x)));

            if (!isValidContainmentBlock(world.getBlock(center.x - x, center.y, center.z - z)))
                return Result.fail(new Vec3Int(-x, 0, -z));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x - x, center.y, center.z - z)));

            if (!isValidContainmentBlock(world.getBlock(center.x - z, center.y, center.z - x)))
                return Result.fail(new Vec3Int(-z, 0, -x));
            else minRating = Math.min(minRating, containmentRatings.get(world.getBlock(center.x - z, center.y, center.z - x)));


            z++;
            if (flag <= 0) {
                flag += 2 * z + 1;
            } else {
                x--;
                flag += 2 * (z - x) + 1;
            }
        }

        return Result.ok(minRating);
    }

    @NotNull
    private boolean[][] loadRune(@NotNull World world, int centerX, int centerY, int centerZ, int radius, @NotNull Direction alignment) {
        boolean[][] runeBlocks = new boolean[2 * radius + 1][2 * radius + 1];

        for (int x = 0; x < runeBlocks.length; x++) {
            for (int z = 0; z < runeBlocks.length; z++) {

                boolean inCircle = (x / 2) * (x / 2) + (z / 2) * (z / 2) <= radius * radius;
                boolean isRune = world.getBlock(centerX - radius + x, centerY, centerZ - radius + z) == this;

                int arrayX;
                int arrayZ;
                switch (alignment) {
                    case NORTH:
                        arrayX = x;
                        arrayZ = z;
                        break;
                    case SOUTH:
                        arrayX = runeBlocks.length - x - 1;
                        arrayZ = runeBlocks.length - z - 1;
                        break;
                    case EAST:
                        arrayX = z;
                        arrayZ = runeBlocks.length - x - 1;
                        break;
                    case WEST:
                    default:
                        arrayX = runeBlocks.length - z - 1;
                        arrayZ = x;
                        break;
                }

                runeBlocks[arrayZ][arrayX] = inCircle && isRune;
            }
        }

        return ArrayUtil.trim(runeBlocks);
    }

    private boolean isValidContainmentBlock(Block blk) {
        return isFocusBlock(blk) || containmentRatings.containsKey(blk);
    }

    private boolean isFocusBlock(@NotNull Block blk) {
        return focusRatings.containsKey(blk);
    }

    /**
     * Finds a closest alignment block in one of the 4 cardinal directions
     *
     * @param x     The x position of the center
     * @param y     The y position of the center
     * @param z     The z position of the center
     * @param world The world to check in
     * @return The Vec3Int containing the position of the alignment block, or <code>null</code>
     */
    @NotNull
    private Optional<Vec3Int> findAlignmentBlock(int x, int y, int z, @NotNull World world) {
        for (int i = 1; i < MAX_FOCUS_RADIUS; i++) {
            if (isFocusBlock(world.getBlock(x - i, y, z))) {
                return Optional.of(new Vec3Int(x - i, y, z));
            }
            if (isFocusBlock(world.getBlock(x + i, y, z))) {
                return Optional.of(new Vec3Int(x + i, y, z));
            }
            if (isFocusBlock(world.getBlock(x, y, z - i))) {
                return Optional.of(new Vec3Int(x, y, z - i));
            }
            if (isFocusBlock(world.getBlock(x, y, z + i))) {
                return Optional.of(new Vec3Int(x, y, z + i));
            }
        }

        return Optional.empty();
    }

    @Override
    public void onNeighborBlockChange(@NotNull World world, int x, int y, int z, @NotNull Block block) {
        if (world.isRemote) return;

        if (!world.getBlock(x, y - 1, z).isOpaqueCube()) {
            // world.breakBlock(coords, true=drop item)
            world.func_147480_a(x, y, z, true);
        }
    }

    @Override
    public void breakBlock(@NotNull World world, int x, int y, int z, @Nullable Block block, int idonteven) {
        super.breakBlock(world, x, y, z, block, idonteven);

        Lively.INSTANCE.runeZoneLookup.getZoneWithPosition(new GlobalCoord(world, x, y, z)).ifPresent(zone -> zone.getAssociatedEffect().remove());
    }

    private int calculateEffectStrength(int radius, int focusPower, int containmentPower) {
        return Math.min(radius * radius * focusPower, radius * radius * containmentPower) / 20;
    }
}
