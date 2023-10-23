package SSM.Attributes;

import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.GameManager;
import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Attribute extends BukkitRunnable implements Listener {

    public enum AbilityUsage {
        LEFT_CLICK("Left-Click"),
        RIGHT_CLICK("Right-Click"),
        BLOCKING("Hold/Release Block"),
        CHARGE_BOW("Charge Bow");

        private String message;

        AbilityUsage(String message) {
            this.message = message;
        }

        public String toString() {
            return message;
        }
    }

    public String name = "No Set Name.";
    protected String[] description = new String[] { ChatColor.RESET + "No Set Description." };
    protected Plugin plugin;
    protected Player owner;
    protected BukkitTask task;
    protected double cooldownTime = 0;
    protected float expUsed = 0;
    protected AbilityUsage usage = AbilityUsage.RIGHT_CLICK;
    protected String useMessage = "You used";

    public Attribute() {
        this.plugin = SSM.getInstance();
    }

    public void checkAndActivate() {
        if(!GameManager.isPlaying()) {
            return;
        }
        if (CooldownManager.getInstance().getRemainingTimeFor(this, owner) <= 0) {
            if (expUsed > 0) {
                if (owner.getExp() < expUsed) {
                    return;
                }
                owner.setExp(owner.getExp() - expUsed);
            }
            CooldownManager.getInstance().addCooldown(this, (long) (cooldownTime * 1000), owner);
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

    public AbilityUsage getUsage() {
        return usage;
    }

    public String getUseMessage() {
        return useMessage;
    }

    public String[] getDescription() {
        return description;
    }

}
