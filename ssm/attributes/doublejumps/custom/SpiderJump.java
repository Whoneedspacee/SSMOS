package ssm.attributes.doublejumps.custom;

import ssm.attributes.Climb;
import ssm.attributes.doublejumps.DirectDoubleJump;
import ssm.managers.KitManager;
import ssm.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class SpiderJump extends DirectDoubleJump {

    protected double energy_to_jump = 0.17;

    public SpiderJump(double power, double height, Sound double_jump_sound) {
        super(power, height, double_jump_sound);
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
    public boolean check() {
        if(owner.getExp() < energy_to_jump) {
            return false;
        }
        return super.check();
    }

    @Override
    public void activate() {
        super.activate();
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
        owner.getWorld().playSound(owner.getLocation(), double_jump_sound, 1f, 1.5f);
    }

}
