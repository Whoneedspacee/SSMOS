package ssm.attributes;

import ssm.managers.BlockRestoreManager;
import ssm.utilities.BlocksUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;

public class ArcticAura extends Attribute {

    protected double range = 5;
    protected long duration_ms = 2000;

    public ArcticAura() {
        super();
        this.name = "Arctic Aura";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "Creates a field of snow around you",
                ChatColor.RESET + "granting +1 damage and 60% knockback",
                ChatColor.RESET + "to opponents standing on it.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Your aura shrinks on low energy.",
        };
        task = this.runTaskTimer(plugin, 0, 0);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        HashMap<Block, Double> blocks = BlocksUtil.getInRadius(owner.getLocation(), range * owner.getExp());
        for(Block block : blocks.keySet()) {
            if(block.getType() == Material.SNOW_BLOCK) {
                continue;
            }
            BlockRestoreManager.ourInstance.snow(block, (byte) 1, (byte) 1, (int) (duration_ms * (1 + blocks.get(block))), 250, 0);
        }
    }

}
