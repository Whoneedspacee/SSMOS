package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class Needler extends Ability implements OwnerRightClickEvent {

    private int needleAmount = 6;
    private int fired = 0;
    private BukkitTask taskID;

    public Needler() {
        super();
        this.name = "Needler";
        this.cooldownTime = 2.5;
        this.usage = AbilityUsage.BLOCKING;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, () ->
        {
            if (!owner.isBlocking() || fired >= 6) {
                fired = 0;
                Bukkit.getScheduler().cancelTask(taskID.getTaskId());
                return;
            }
            Arrow arrow = owner.getWorld().spawnArrow(owner.getEyeLocation().add(owner.getLocation().getDirection()),
                    owner.getLocation().getDirection(), 1.2f, 6);
            arrow.setShooter(owner);
            arrow.setCustomName("Needler");
            arrow.setMetadata("Needler", new FixedMetadataValue(plugin, 1));
            owner.playSound(owner.getLocation(), Sound.SPIDER_IDLE, 0.8f, 2f);
            fired++;
        }, 0L, 0L);
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                List<MetadataValue> data = arrow.getMetadata("Needler");
                if (data.size() > 0) {
                    e.setCancelled(true);
                    DamageUtil.damage((LivingEntity) e.getEntity(), (LivingEntity) arrow.getShooter(), 1.1,
                            1.0, false, EntityDamageEvent.DamageCause.CUSTOM, null, true);
                }
            }
        }
    }
}
