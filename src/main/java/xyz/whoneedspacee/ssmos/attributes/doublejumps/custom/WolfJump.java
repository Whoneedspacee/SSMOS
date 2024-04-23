package xyz.whoneedspacee.ssmos.attributes.doublejumps.custom;

import xyz.whoneedspacee.ssmos.attributes.doublejumps.DirectDoubleJump;
import org.bukkit.Sound;

public class WolfJump extends DirectDoubleJump {

    public WolfJump(double power, double height, Sound double_jump_sound) {
        super(power, height, double_jump_sound);
        this.name = "Wolf Jump";
        this.usage = AbilityUsage.DOUBLE_JUMP;
    }

}
