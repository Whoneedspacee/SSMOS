package ssm.attributes.doublejumps.custom;

import ssm.attributes.doublejumps.EnergyDoubleJump;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

public class ChickenJump extends EnergyDoubleJump {

    public ChickenJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound, float expUsed) {
        super(power, height, maxDoubleJumps, doubleJumpSound, expUsed);
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
    public void playDoubleJumpSound() {
        owner.getWorld().playSound(owner.getLocation(), doubleJumpSound, (float) (0.3 + owner.getExp()), (float) (Math.random() / 2 + 1));
    }

}
