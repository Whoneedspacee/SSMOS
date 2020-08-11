package SSM.Abilities;

import SSM.Ability;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Fissure extends Ability implements OwnerRightClickEvent {

    private int fissureLength = 13;
    private int task = -1;
    private int cycle = 0;
    private Material type;
    private ArrayList<Location> blocks = new ArrayList<>();

    public Fissure() {
        super();
        this.name = "Fissure";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        Location loc = owner.getLocation();
        Vector dir = loc.getDirection();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    cycle++;
                    layerOne(dir, loc);
                    if (cycle >= fissureLength) {
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
        blocks.add(new Location(loc.getWorld(), loc.getX(), loc.getY() + 1, loc.getZ()));
    }

    private void stop() {
        Bukkit.getScheduler().cancelTask(task);
    }

}
