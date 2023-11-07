package SSM.Attributes;

import SSM.GameManagers.BlockRestoreManager;
import SSM.Utilities.BlocksUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class ArcticAura extends Attribute {

    private double range = 5;
    private double duration = 2;

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
            BlockRestoreManager.ourInstance.snow(block, (byte) 1, (byte) 1, (int) (duration * (1 + blocks.get(block))), 250, 0);
        }
    }

}
