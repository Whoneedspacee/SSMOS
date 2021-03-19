package SSM.Attributes.DoubleJumps;

import org.bukkit.Sound;

public class DirectDoubleJump extends DoubleJump {

    public DirectDoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
    }

    @Override
    protected void jump() {
        double frictionModifier = perfectJumped ? -0.2 : 0;
        owner.setVelocity(owner.getLocation().getDirection().multiply(power));//.add(new Vector(0, height, 0)));
    }

}
