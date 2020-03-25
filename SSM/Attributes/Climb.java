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
        this.name = "Item Generator";
        this.power = power;
        this.runTaskTimer(plugin, 0, 1);
    }

    public void activate() {
        if (owner.isSneaking()){
            if (!owner.getLocation().getBlock().getRelative(BlockFace.WEST).isPassable() || !owner.getLocation().getBlock().getRelative(BlockFace.SOUTH).isPassable() || !owner.getLocation().getBlock().getRelative(BlockFace.EAST).isPassable() || !owner.getLocation().getBlock().getRelative(BlockFace.NORTH).isPassable()){
                owner.setVelocity(new Vector(0, power, 0));
            }
        }
    }


    @Override
    public void run() {
        activate();
    }

}
