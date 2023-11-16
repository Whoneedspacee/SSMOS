package ssm.attributes.doublejumps.custom;

import ssm.attributes.Climb;
import ssm.attributes.doublejumps.DirectDoubleJump;
import ssm.gamemanagers.KitManager;
import ssm.kits.Kit;
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
        Climb climb = kit.getAttributeByClass(Climb.class);
        if(climb == null) {
            return;
        }
        climb.last_cooldown_time = System.currentTimeMillis();
    }

    @Override
    public void playDoubleJumpSound() {
        return;
    }

}
