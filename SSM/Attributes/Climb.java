package SSM.Attributes;

import SSM.Attributes.DoubleJumps.DoubleJump;
import SSM.GameManagers.KitManager;
import SSM.Utilities.BlocksUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Climb extends Attribute {

    protected double power;
    protected boolean chargedDoubleJump;
    public long last_cooldown_time = 0;
    public long cooldown_time_ms = 150;

    public Climb(double power) {
        super();
        this.name = "Wall Climb";
        this.usage = AbilityUsage.CROUCH;
        this.description = new String[] {
                ChatColor.RESET + "While crouching, you climb up walls.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Climbing a wall allows you to use",
                ChatColor.RESET + "Spider Leap one more time.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Wall Climb uses Energy (Experience Bar)",
        };
        this.expUsed = 0.005f;
        this.power = power;
        this.chargedDoubleJump = false;
        task = this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if (Utils.entityIsOnGround(owner)) {
            chargedDoubleJump = false;
        }
        if (!owner.isSneaking()) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        if(System.currentTimeMillis() - last_cooldown_time < cooldown_time_ms) {
            return;
        }
        for (Block block : BlocksUtil.getBlocks(owner.getLocation(), 1)) {
            if (block.getType().isSolid() && !block.isLiquid()) {
                VelocityUtil.setVelocity(owner, new Vector(0, power, 0));
                if (!chargedDoubleJump) {
                    KitManager.getPlayerKit(owner).getAttributes().forEach(attribute -> {
                        if (attribute instanceof DoubleJump) {
                            DoubleJump dj = (DoubleJump) attribute;
                            dj.resetDoubleJumps();
                            chargedDoubleJump = true;
                        }
                    });
                }
                break;
            }
        }
    }

}
