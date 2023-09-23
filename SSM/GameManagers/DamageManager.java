package SSM.GameManagers;

import SSM.Kits.Kit;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DamageManager implements Listener {

    private static DamageManager ourInstance;
    private JavaPlugin plugin = SSM.getInstance();

    public DamageManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    // High priority to handle all damage events after all additions
    @EventHandler(priority = EventPriority.HIGHEST)
    public void customDamage(EntityDamageByEntityEvent e) {
        // Something else didn't want this to happen and we are going to listen
        if(e.isCancelled()) {
            return;
        }
        // Cancel the event since we're going to handle all the information
        Entity entity = e.getDamager();
        e.setCancelled(true);
        if(!DamageUtil.canDamage((LivingEntity) e.getEntity())) {
            return;
        }
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Kit playerKit = KitManager.getPlayerKit(player);
            if(playerKit == null) {
                return;
            }
            if(e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
                DamageUtil.damage((LivingEntity) e.getEntity(), (LivingEntity) e.getDamager(), playerKit.getMelee());
            }
        }
        else if(entity instanceof Projectile) {
            Projectile projectile = (Projectile) entity;
            LivingEntity livingEntity = null;
            if(projectile.getShooter() instanceof LivingEntity) {
                livingEntity = (LivingEntity) projectile.getShooter();
            }
            DamageUtil.damage((LivingEntity) e.getEntity(), livingEntity, e.getDamage(),
                    1.0, false, EntityDamageEvent.DamageCause.CUSTOM, entity.getLocation());
            if(projectile instanceof Arrow && projectile.getShooter() instanceof Player) {
                List<MetadataValue> data = projectile.getMetadata("Needler");
                if(data.size() > 0) {
                    return;
                }
                Player player = (Player) projectile.getShooter();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 0.5f);
            }
        }
        else {
            Bukkit.broadcastMessage("Unhandled Cause: " + e.getCause().toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clearArrow(ProjectileHitEvent e) {
        if(e.getEntity() instanceof Arrow) {
            if(e.getHitEntity() != null) {
                e.getEntity().remove();
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Arrow arrow = (Arrow) e.getEntity();
                    arrow.remove();
                }
            }, 300L);
        }
    }

}
