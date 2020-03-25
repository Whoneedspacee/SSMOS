package SSM;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Attribute extends BukkitRunnable implements Listener {

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

    public void remove() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        HandlerList.unregisterAll(this);
    }
}
