package ssm.attributes.doublejumps;

import ssm.utilities.VelocityUtil;
import org.bukkit.Sound;

public class EnergyDoubleJump extends DoubleJump {

    // We need to use a custom exp usage variable here because we subtract after usage
    protected float expUsed;

    public EnergyDoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound, float expUsed) {
        super(power, height, maxDoubleJumps, doubleJumpSound);
        this.name = "Energy Double Jump";
        this.recharge_air_ticks = 2;
        this.expUsed = expUsed;
        this.needs_xp = true;
    }

    @Override
    protected void jump() {
        // For some reason player setVelocity results in the sent packet being reduced
        // For no real consistent reason due to serverside predictions more than likely
        // Rather than delve into that, it was far easier to just send the values
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), power, true, power, 0.15, height, true);
        owner.setExp(Math.max(0f, owner.getExp() - expUsed));
    }

}
