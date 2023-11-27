package ssm.attributes.doublejumps;

import ssm.utilities.VelocityUtil;
import org.bukkit.Sound;

public class GenericDoubleJump extends DoubleJump {

    public GenericDoubleJump(double power, double height, Sound double_jump_sound) {
        super(power, height, double_jump_sound);
        this.name = "Double Jump";
    }

    @Override
    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), power, true, power, 0, height, true);
    }

}
