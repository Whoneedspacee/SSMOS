package SSM;

import SSM.GameManagers.CooldownManager;
import org.bukkit.Bukkit;
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
    protected double cooldownTime = 0;
    protected float expUsed = 0;

    public Attribute() {
        this.plugin = SSM.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void checkAndActivate() {
        if (CooldownManager.getInstance().getRemainingTimeFor(name, owner) <= 0) {
            if (expUsed > 0) {
                if (owner.getExp() < expUsed) {
                    return;
                }
                owner.setExp(owner.getExp() - expUsed);
            }
            CooldownManager.getInstance().addCooldown(name, (long) (cooldownTime * 1000), owner);
            activate();
        }
    }

    public abstract void activate();

    public void remove() {
        this.setOwner(null);
        cancelTask();
        HandlerList.unregisterAll(this);
    }

    public boolean cancelTask() {
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        this.cancel();
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

}
