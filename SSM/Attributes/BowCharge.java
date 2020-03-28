package SSM.Attributes;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class BowCharge extends Attribute {

    double delay;
    double rate;
    int maxCharge;

    double charge = 0;
    ItemStack chargingBow;

    public BowCharge(double delay, double rate, int maxCharge) {
        super();
        this.name = "Bow Charge";
        this.delay = delay;
        this.rate = rate;
        this.maxCharge = maxCharge;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void drawBow(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player != owner) {
            return;
        }
        if (player.getInventory().getItemInMainHand().getType() != Material.BOW) {
            return;
        }
        if (!player.getInventory().contains(Material.ARROW)) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            chargingBow = player.getInventory().getItemInMainHand();
            task = this.runTaskTimer(plugin, (long) delay * 20, (long) rate * 20);
        }
    }

    public void switchedWeapon(PlayerItemHeldEvent e) {
        if(task != null)
        task.cancel();
    }

    public boolean incrementCharge() {
        if (charge <= maxCharge) {
            charge++;
            return true;
        }
        return false;
    }

}

