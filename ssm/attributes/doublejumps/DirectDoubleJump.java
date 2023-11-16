package ssm.attributes.doublejumps;

import ssm.utilities.VelocityUtil;
import org.bukkit.Sound;

public class DirectDoubleJump extends DoubleJump {

    public DirectDoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
    }

    @Override
    protected void jump() {
        // For some reason player setVelocity results in the sent packet being reduced
        // For no real consistent reason due to serverside predictions more than likely
        // Rather than delve into that, it was far easier to just send the values
        VelocityUtil.setVelocity(owner, power, 0.2, height, true);
    }

}
