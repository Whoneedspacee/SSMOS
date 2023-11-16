package ssm.attributes.doublejumps.custom;

import ssm.attributes.doublejumps.DirectDoubleJump;
import org.bukkit.Sound;

public class WolfJump extends DirectDoubleJump {

    public WolfJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
        this.name = "Wolf Jump";
        this.usage = AbilityUsage.DOUBLE_JUMP;
    }

}
