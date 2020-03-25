package SSM.Attributes;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.List;

public class ClearProjectile extends Attribute {

    public ClearProjectile() {
        super();
        this.name = "Projectile Clearer";
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // call it last so it deletes the projectile after everything
    @EventHandler(priority = EventPriority.HIGHEST)
    public void clearArrow(ProjectileHitEvent e) {
        Projectile projectile = e.getEntity();
        ProjectileSource firer = projectile.getShooter();
        if (projectile == null || firer != owner) {
            return;
        }
        projectile.remove();
    }

}
