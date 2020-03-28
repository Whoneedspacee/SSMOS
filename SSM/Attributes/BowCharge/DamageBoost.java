package SSM.Attributes.BowCharge;

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

public class DamageBoost extends BowCharge {

    public DamageBoost(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Charge Bow Damage";
    }

    @Override
    public void firedBow(Arrow arrow) {
        arrow.setDamage(arrow.getDamage() + charge);
    }

}

