package SSM.Utilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BlocksUtil {

    public static HashSet<Byte> blockUseSet = new HashSet<Byte>();

    static {
        blockUseSet.add((byte) Material.DISPENSER.getId());
        blockUseSet.add((byte) Material.BED_BLOCK.getId());
        blockUseSet.add((byte) Material.PISTON_BASE.getId());
        blockUseSet.add((byte) Material.BOOKSHELF.getId());
        blockUseSet.add((byte) Material.CHEST.getId());
        blockUseSet.add((byte) Material.WORKBENCH.getId());
        blockUseSet.add((byte) Material.FURNACE.getId());
        blockUseSet.add((byte) Material.BURNING_FURNACE.getId());
        blockUseSet.add((byte) Material.WOODEN_DOOR.getId());
        blockUseSet.add((byte) Material.LEVER.getId());
        blockUseSet.add((byte) Material.IRON_DOOR_BLOCK.getId());
        blockUseSet.add((byte) Material.STONE_BUTTON.getId());
        blockUseSet.add((byte) Material.FENCE.getId());
        blockUseSet.add((byte) Material.DIODE_BLOCK_OFF.getId());
        blockUseSet.add((byte) Material.DIODE_BLOCK_ON.getId());
        blockUseSet.add((byte) Material.TRAP_DOOR.getId());
        blockUseSet.add((byte) Material.FENCE_GATE.getId());
        blockUseSet.add((byte) Material.NETHER_FENCE.getId());
        blockUseSet.add((byte) Material.ENCHANTMENT_TABLE.getId());
        blockUseSet.add((byte) Material.BREWING_STAND.getId());
        blockUseSet.add((byte) Material.ENDER_CHEST.getId());
        blockUseSet.add((byte) Material.ANVIL.getId());
        blockUseSet.add((byte) Material.TRAPPED_CHEST.getId());
        blockUseSet.add((byte) Material.HOPPER.getId());
        blockUseSet.add((byte) Material.DROPPER.getId());

        blockUseSet.add((byte) Material.BIRCH_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.JUNGLE_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.DARK_OAK_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.ACACIA_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.SPRUCE_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.BIRCH_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.JUNGLE_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.DARK_OAK_FENCE_GATE.getId());
        blockUseSet.add((byte) Material.ACACIA_FENCE_GATE.getId());

        blockUseSet.add((byte) Material.SPRUCE_DOOR.getId());
        blockUseSet.add((byte) Material.BIRCH_DOOR.getId());
        blockUseSet.add((byte) Material.JUNGLE_DOOR.getId());
        blockUseSet.add((byte) Material.ACACIA_DOOR.getId());
        blockUseSet.add((byte) Material.DARK_OAK_DOOR.getId());
    }

    public static boolean isUsable(Block block) {
        return blockUseSet.contains(block.getTypeId());
    }

    public static List<Block> getBlocks(Location start, int radius) {
        if (radius <= 0) {
            return new ArrayList<Block>(0);
        }
        int iterations = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<Block>(iterations * iterations * iterations);
        blocks.add((Block) start.getBlock());
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add((Block) start.getBlock().getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }

    public static HashMap<Block, Double> getInRadius(Location location, double dR, double maxHeight) {
        HashMap<Block, Double> blockList = new HashMap<Block, Double>();
        int iR = (int) dR + 1;
        for (int x = -iR; x <= iR; x++) {
            for (int z = -iR; z <= iR; z++) {
                for (int y = -iR; y <= iR; y++) {
                    if (Math.abs(y) > maxHeight) {
                        continue;
                    }
                    Block curBlock = location.getWorld().getBlockAt(
                            (int) (location.getX() + x), (int) (location.getY() + y), (int) (location.getZ() + z));
                    double offset = location.distance(curBlock.getLocation().add(0.5, 0.5, 0.5));
                    if (offset <= dR) {
                        blockList.put(curBlock, 1 - (offset / dR));
                    }
                }
            }
        }
        return blockList;
    }

}
