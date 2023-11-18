package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class Needler extends Ability implements OwnerRightClickEvent {

    protected int needleAmount = 6;
    private int fired = 0;
    private BukkitTask taskID;

    public Needler() {
        super();
        this.name = "Needler";
        this.cooldownTime = 2.5;
        this.usage = AbilityUsage.BLOCKING;
        this.description = new String[] {
                ChatColor.RESET + "Quickly spray up to 5 needles from ",
                ChatColor.RESET + "your mouth, dealing damage and small",
                ChatColor.RESET + "knockback to opponents.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, () ->
        {
            if (owner == null || !owner.isBlocking() || fired >= needleAmount) {
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
    public void arrowHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow && e.getHitEntity() instanceof LivingEntity) {
            Arrow arrow = (Arrow) e.getEntity();
            if (!arrow.getShooter().equals(owner)) {
                return;
            }
            LivingEntity livingEntity = (LivingEntity) e.getHitEntity();
            List<MetadataValue> data = arrow.getMetadata("Needler");
            if (data.size() > 0) {
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, (LivingEntity) arrow.getShooter(), 1.1);
                smashDamageEvent.setIgnoreDamageDelay(true);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
                arrow.remove();
                if(DamageUtil.canDamage(livingEntity, (LivingEntity) arrow.getShooter())) {
                    // Remove the poison so we can re-apply
                    livingEntity.removePotionEffect(PotionEffectType.POISON);
                    PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 40, 0, false, false);
                    livingEntity.addPotionEffect(poison);
                    livingEntity.setMetadata("Poison Damager", new FixedMetadataValue(plugin, owner));
                }
            }
        }
    }

    // Cancel the event after ignoredamagerate is checked so arrows dont deal damage
    // This is also so arrows add a damage rate cooldown even if the custom needler arrows ignore it
    @EventHandler
    public void arrowDamage(SmashDamageEvent e) {
        if (e.getDamageCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getProjectile();
                List<MetadataValue> data = arrow.getMetadata("Needler");
                if (data.size() > 0) {
                    e.setCancelled(true);
                }
            }
        }
    }

}
