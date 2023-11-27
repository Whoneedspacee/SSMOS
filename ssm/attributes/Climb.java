package ssm.attributes;

import ssm.attributes.doublejumps.DoubleJump;
import ssm.managers.KitManager;
import ssm.utilities.BlocksUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Climb extends Attribute {

    protected double power;
    protected boolean chargedDoubleJump;
    public long last_use_time = 0;
    public long last_cooldown_time = 0;
    public long doublejump_cooldown_time_ms = 150;
    public long cooldown_time_ms = 75;

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
        if(System.currentTimeMillis() - last_cooldown_time < doublejump_cooldown_time_ms) {
            return;
        }
        if(System.currentTimeMillis() - last_use_time < cooldown_time_ms) {
            return;
        }
        last_use_time = System.currentTimeMillis();
        for (Block block : BlocksUtil.getBlocks(owner.getLocation(), 1)) {
            if (block.getType().isSolid() && !block.isLiquid()) {
                VelocityUtil.setVelocity(owner, new Vector(0, power, 0));
                if (!chargedDoubleJump) {
                    KitManager.getPlayerKit(owner).getAttributes().forEach(attribute -> {
                        if (attribute instanceof DoubleJump) {
                            DoubleJump dj = (DoubleJump) attribute;
                            owner.setAllowFlight(true);
                            chargedDoubleJump = true;
                        }
                    });
                }
                break;
            }
        }
    }

}
