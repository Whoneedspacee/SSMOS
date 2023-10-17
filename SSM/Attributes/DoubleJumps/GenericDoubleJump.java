package SSM.Attributes.DoubleJumps;

import SSM.Utilities.VelocityUtil;
import org.bukkit.Sound;

public class GenericDoubleJump extends DoubleJump {

    public GenericDoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
        this.name = "Double Jump";
    }

    @Override
    protected void jump() {
        // For some reason player setVelocity results in the sent packet being reduced
        // For no real consistent reason due to serverside predictions more than likely
        // Rather than delve into that, it was far easier to just send the values
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), power, true, power, 0, height, true);
    }

}
