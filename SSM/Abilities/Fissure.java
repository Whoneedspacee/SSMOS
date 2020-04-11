package SSM.Abilities;

import SSM.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Fissure extends Ability {

    private int fissureLength = 13;
    private int task = -1;
    private int cycle = 0;
    private Material type;
    private ArrayList<Location> blocks = new ArrayList<>();

    public Fissure() {
        super();
        this.name = "Fissure";
        this.cooldownTime = 10;
        this.rightClickActivate = true;
    }

    public void activate() {
        Location loc = owner.getLocation();
        Vector dir = loc.getDirection();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            @Override
            public void run(){
                cycle++;
                layerOne(dir, loc);
                if (cycle >= fissureLength){
                    stop();
                    cycle = 0;
                }
            }
        }, 0L, 2L
        );
    }


    public void layerOne(Vector dir, Location loc) {
        dir.multiply(1);
        loc.add(dir);
        loc.setY(owner.getLocation().getY());
        type = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ()).getBlock().getType();
        loc.getBlock().setType(type);
        blocks.add(loc);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                layerTwo(loc);
            }
        }, 1L);
    }

    public void layerTwo(Location loc) {
        loc.getWorld().getBlockAt(new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ())).setType(type);
        blocks.add(new Location(loc.getWorld(), loc.getX(), loc.getY()+1, loc.getZ()));
    }


    private void stop(){
        Bukkit.getScheduler().cancelTask(task);
    }


}
