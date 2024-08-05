package xyz.whoneedspacee.ssmos.attributes.doublejumps.custom;

import xyz.whoneedspacee.ssmos.attributes.doublejumps.EnergyDoubleJump;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;

public class ChickenJump extends EnergyDoubleJump {

    public ChickenJump(double power, double height, Sound double_jump_sound, float expUsed) {
        super(power, height, double_jump_sound, expUsed);
        this.name = "Flap";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "You are able to use your double jump",
                ChatColor.RESET + "up to 6 times in a row.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Flap uses Energy (Experience Bar)",
        };
    }

    @Override
    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), power, true, power, 0.15, height, true);
    }

    @Override
    public void playDoubleJumpSound() {
        owner.getWorld().playSound(owner.getLocation(), double_jump_sound, (float) (0.3 + owner.getExp()), (float) (Math.random() / 2 + 1));
    }

}
