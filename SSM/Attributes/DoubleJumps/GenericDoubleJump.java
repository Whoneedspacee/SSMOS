package SSM.Attributes.DoubleJumps;

public class GenericDoubleJump extends DoubleJump {

    public GenericDoubleJump(double power, double height, int maxDoubleJumps) {
        super(power, height, maxDoubleJumps);
    }

    @Override
    protected void jump(boolean perfectJumped) {
        double frictionModifier = perfectJumped ? - 0.2 : 0;

        owner.setVelocity(owner.getLocation().getDirection().multiply(power).setY(height + frictionModifier));
    }
}
