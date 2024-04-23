package xyz.whoneedspacee.ssmos.attributes.doublejumps;

import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import org.bukkit.Sound;

public class DirectDoubleJump extends DoubleJump {

    public DirectDoubleJump(double power, double height, Sound doubleJumpSound) {
        super(power, height, doubleJumpSound);
    }

    @Override
    public void activate() {
        VelocityUtil.setVelocity(owner, power, 0.2, height, true);
    }

}
