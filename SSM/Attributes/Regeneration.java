package SSM.Attributes;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.PacketPlayOutUpdateHealth;
import org.bukkit.EntityEffect;

public class Regeneration extends Attribute {

    Double regen;
    double delay;

    public Regeneration(Double regen, long delay) {
        super();
        this.name = "Regeneration";
        this.regen = regen;
        this.delay = delay;
        task = this.runTaskTimer(plugin, 0, delay);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        if (!owner.isDead()) {
            // Use packets here to make the visual effect on the healthbar appear
            owner.setHealth(Math.min(owner.getHealth() + regen, 20));
            PacketPlayOutUpdateHealth packet = new PacketPlayOutUpdateHealth((float) owner.getHealth(), owner.getFoodLevel(), owner.getSaturation());
            Utils.sendPacket(owner, packet);
        }
    }

}
