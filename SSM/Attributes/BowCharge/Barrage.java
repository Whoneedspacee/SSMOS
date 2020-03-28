package SSM.Attributes.BowCharge;

import SSM.*;
import org.bukkit.Bukkit;
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

public class Barrage extends BowCharge {

    public Barrage(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Barrage";
    }

    public void firedBow(Arrow p) {
        double initialVelocity = p.getVelocity().length();
        for (int i = 0; i < charge; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Arrow arrow = owner.launchProjectile(Arrow.class, owner.getLocation().getDirection().multiply(initialVelocity));
                    arrow.setCustomName("Barrage Arrow");
                    owner.playSound(owner.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }, (i + 1) * 2);
        }
    }

}

