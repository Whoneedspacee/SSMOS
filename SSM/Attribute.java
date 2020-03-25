package SSM;

import SSM.GameManagers.CooldownManager;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class Attribute extends BukkitRunnable implements Listener {

    public String name = "Base";

    protected Plugin plugin;
    protected Player owner;

    protected BukkitTask task;

    public Attribute() {
        this.plugin = SSM.getInstance();
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public void activate() {
        // it just doesn't feel like its worth the effort to make this abstract right now tbh
    }

    public void destroyAttribute() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        HandlerList.unregisterAll(this);
    }

    @Override
    public void run() {
        this.cancel();
    }

}
