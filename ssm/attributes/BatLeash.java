package ssm.attributes;

import org.bukkit.ChatColor;

public class BatLeash extends Attribute {

    public BatLeash() {
        super();
        this.name = "Bat Leash";
        this.usage = AbilityUsage.DOUBLE_RIGHT_CLICK;
        this.description = new String[]{
                ChatColor.RESET + "Attach a rope to your wave of bats,",
                ChatColor.RESET + "causing you to be pulled behind them!",
        };
    }

    public void activate() { }

}
