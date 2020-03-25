package SSM.Attributes;

import SSM.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

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
