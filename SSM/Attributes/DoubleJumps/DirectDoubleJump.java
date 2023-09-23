package SSM.Attributes.DoubleJumps;

import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.util.Vector;

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
