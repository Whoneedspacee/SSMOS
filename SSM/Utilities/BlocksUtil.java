package SSM.Utilities;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlocksUtil {

    public static List<Block> getBlocks(Location start, int radius){
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

}
