package SSM.Attributes;

import SSM.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Climb extends Attribute {

    double power;

    public Climb(double power) {
        super();
        this.name = "Spider Climb";
        this.power = power;
        task = this.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public void run() {
        if (!owner.isSneaking()) {
            return;
        }
        for (BlockFace face : BlockFace.values()) {
            if (!owner.getLocation().getBlock().getRelative(face).isPassable()) {
                owner.setVelocity(new Vector(0, power, 0));
                break;
            }
        }
    }

}
