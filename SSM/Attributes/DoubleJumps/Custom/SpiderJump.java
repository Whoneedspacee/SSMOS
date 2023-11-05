package SSM.Attributes.DoubleJumps.Custom;

import SSM.Attributes.Climb;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import SSM.Utilities.VelocityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class SpiderJump extends DirectDoubleJump {

    private double energy_per_jump = 0.17;

    public SpiderJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
        this.name = "Spider Leap";
        this.usage = AbilityUsage.DOUBLE_JUMP;
        this.description = new String[] {
                ChatColor.RESET + "Your double jump is special. It goes",
                ChatColor.RESET + "exactly in the direction you are looking.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Spider Leap uses Energy (Experience Bar)",
        };
    }

    @Override
    protected void jump() {
        if(owner.getExp() < energy_per_jump) {
            return;
        }
        super.jump();
        owner.getWorld().playSound(owner.getLocation(), doubleJumpSound, 1f, 1.5f);
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit == null) {
            return;
        }
        Climb climb = (Climb) kit.getAttributeByClass(Climb.class);
        if(climb == null) {
            return;
        }
        climb.cooldown_ticks = 0;
    }

    @Override
    public void playDoubleJumpSound() {
        return;
    }

}
