package SSM.Attributes.DoubleJumps;

import org.bukkit.Sound;

public class GenericDoubleJump extends DoubleJump {

    public GenericDoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
        this.name = "Double Jump";
    }

    @Override
    protected void jump() {
        double frictionModifier = perfectJumped ? -0.2 : 0;
        owner.setVelocity(owner.getLocation().getDirection().multiply(power).setY(height + frictionModifier));
    }

}
