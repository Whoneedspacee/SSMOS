package SSM.Attributes.DoubleJumps;

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
        Vector direction = owner.getLocation().getDirection();
        direction.normalize();
        direction.multiply(power);
        direction.setY(direction.getY() + 0.2);
        if(direction.getY() >= height) {
            direction.setY(height);
        }
        if(perfectJumped) {
            direction.setY(direction.getY() + 0.2);
        }
        // For some reason player setVelocity results in the sent packet being reduced
        // For no real consistent reason due to serverside predictions more than likely
        // Rather than delve into that, it was far easier to just send the values
        ((CraftPlayer) owner).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(owner.getEntityId(),
                direction.getX(), direction.getY(), direction.getZ()));
    }

}
