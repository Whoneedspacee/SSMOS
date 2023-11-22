package ssm.attributes;

import org.bukkit.Bukkit;
import ssm.managers.CooldownManager;
import ssm.managers.KitManager;
import ssm.kits.Kit;
import ssm.Main;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
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
        HOLD_BLOCKING("Hold Block"),
        CHARGE_BOW("Charge Bow"),
        CROUCH("Crouch"),
        DOUBLE_JUMP("Double Jump"),
        PASSIVE("Passive"),
        DOUBLE_RIGHT_CLICK("Double Right-Click"),
        RIGHT_CLICK_DROP("Right-Click/Drop"),
        SMASH_CRYSTAL("Smash Crystal");


        private String message;

        AbilityUsage(String message) {
            this.message = message;
        }

        public String toString() {
            return message;
        }
    }

    public String name = "No Set Name.";
    public String item_name = null;
    protected String[] description = new String[]{ChatColor.RESET + "No Set Description."};
    protected Plugin plugin;
    protected Player owner;
    protected BukkitTask task;
    protected double cooldownTime = 0;
    protected float expUsed = 0;
    protected AbilityUsage usage = AbilityUsage.RIGHT_CLICK;
    protected String useMessage = "You used";
    protected boolean inform_cooldown = true;
    protected long last_inform_time = 0;

    public Attribute() {
        this.plugin = Main.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean check() {
        if (owner == null) {
            return false;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if (kit != null && !kit.isActive()) {
            return false;
        }
        if (hasCooldown()) {
            return false;
        }
        if (expUsed > 0 && owner.getExp() < expUsed) {
            return false;
        }
        return true;
    }

    public void checkAndActivate() {
        Kit kit = KitManager.getPlayerKit(owner);
        if (kit != null && !kit.isActive()) {
            return;
        }
        if (hasCooldown() && inform_cooldown && System.currentTimeMillis() - last_inform_time >= 50) {
            String time_string = String.format("%.1f seconds", CooldownManager.getInstance().getRemainingTimeFor(this, owner) / 1000.0);
            Utils.sendAttributeMessage("You cannot use " + ChatColor.GREEN + name +
                    ChatColor.GRAY + " for", time_string, owner, ServerMessageType.RECHARGE);
            last_inform_time = System.currentTimeMillis();
            return;
        }
        if (!hasCooldown()) {
            if (!check()) {
                return;
            }
            if (expUsed > 0) {
                owner.setExp(Math.max(owner.getExp() - expUsed, 0));
            }
            applyCooldown();
            activate();
        }
    }

    public abstract void activate();

    public boolean hasCooldown() {
        return (CooldownManager.getInstance().getRemainingTimeFor(this, owner) > 0);
    }

    public void applyCooldown() {
        applyCooldown(cooldownTime);
    }

    public void applyCooldown(double cooldownTime) {
        CooldownManager.getInstance().addCooldown(this, (long) (cooldownTime * 1000), owner);
    }

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
        cancelTask();
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
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
