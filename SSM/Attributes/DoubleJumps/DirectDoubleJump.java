package SSM.Attributes.DoubleJumps;

import org.bukkit.util.Vector;

public class DirectDoubleJump extends DoubleJump {

    public DirectDoubleJump(double power, double height, int maxDoubleJumps) {
        super(power, height, maxDoubleJumps);
    }

    @Override
    protected void jump(boolean perfectJumped) {
        double frictionModifier = perfectJumped ? -0.2 : 0;

        owner.setVelocity(owner.getLocation().getDirection().multiply(power));//.add(new Vector(0, height, 0)));
    }
}
