package xyz.whoneedspacee.ssmos.abilities.boss;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.utilities.DamageUtil;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;

import java.util.ArrayList;
import java.util.List;

public class WitherArmy extends Ability implements OwnerRightClickEvent {

    protected double target_radius = 15;
    protected int skeleton_amount = 8;
    protected long launch_delay_ticks = 2L;
    public List<Skeleton> wither_army = new ArrayList<Skeleton>();

    public WitherArmy() {
        super();
        this.name = "Wither Army";
        this.cooldownTime = 16;
        this.description = new String[] {
                ChatColor.RESET + "Create an army of wither skeletons.",
                ChatColor.RESET + "The skeletons are launched forwards with",
                ChatColor.RESET + "high speeds. Lasts 8 seconds.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.WITHER_SPAWN, 1f, 1f);
        for(int i = 0; i < skeleton_amount; i++) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    final Skeleton wither_soldier = (Skeleton) owner.getWorld().spawnEntity(owner.getEyeLocation(), EntityType.SKELETON);
                    wither_army.add(wither_soldier);
                    wither_soldier.setSkeletonType(Skeleton.SkeletonType.WITHER);
                    wither_soldier.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
                    wither_soldier.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                    wither_soldier.setMaxHealth(10);
                    wither_soldier.setHealth(wither_soldier.getMaxHealth());
                    getNewTarget(wither_soldier, false);
                    wither_soldier.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 1, false, false));
                    //double shoot_angle = finalI * 2 * Math.PI / skeleton_amount;
                    //Vector shoot_direction = new Vector(Math.cos(shoot_angle), 1, Math.sin(shoot_angle)).normalize();
                    //VelocityUtil.setVelocity(wither_soldier, shoot_direction.multiply(0.3));
                    VelocityUtil.setVelocity(wither_soldier, owner.getLocation().getDirection(),
                            1.6, false, 0, 0.2, 10, true);
                    // Update every 10 ticks
                    BukkitRunnable runnable = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (owner == null || !owner.isValid() || !wither_soldier.isValid() ||
                                    wither_soldier.getTicksLived() > 160 || wither_soldier.getLocation().getBlock().isLiquid()) {
                                Utils.itemEffect(wither_soldier.getLocation().add(0, 0.5, 0), 12, 0.3,
                                        Sound.WITHER_HURT, 1f, 0.75f, Material.BONE, (byte) 0, 40);
                                wither_soldier.remove();
                                wither_army.remove(wither_soldier);
                                cancel();
                                return;
                            }
                            getNewTarget(wither_soldier, true);
                        }
                    };
                    runnable.runTaskTimer(plugin, 10L, 10L);
                }
            }, launch_delay_ticks * i);
        }
    }

    public void getNewTarget(Skeleton skeleton, boolean throw_towards) {
        // Get Nearby is sorted by distance so this should target the closest one
        for(Player player : Utils.getNearby(skeleton.getLocation(), target_radius)) {
            if(player.equals(owner)) {
                continue;
            }
            if(!GameManager.isAlive(player)) {
                continue;
            }
            skeleton.setTarget(player);
            if(throw_towards) {
                Vector trajectory = player.getLocation().subtract(skeleton.getLocation()).toVector().normalize();
                VelocityUtil.setVelocity(skeleton, trajectory, 0.9, false, 0, 0.2, 10, true);
            }
            break;
        }
    }

    @EventHandler
    public void entityTarget(EntityTargetEvent e) {
        if(!(e.getEntity() instanceof Skeleton)) {
            return;
        }
        if(!wither_army.contains((Skeleton) e.getEntity())) {
            return;
        }
        if(e.getTarget() == null) {
            return;
        }
        if(e.getTarget().equals(owner)) {
            e.setCancelled(true);
        }
        if(!(e.getTarget() instanceof LivingEntity)) {
            return;
        }
        if(!DamageUtil.canDamage((LivingEntity) e.getTarget(), owner)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void checkImageDamage(SmashDamageEvent e) {
        if(e.getDamagee() instanceof Skeleton) {
            if(wither_army.contains((Skeleton) e.getDamagee())) {
                if(e.getDamageCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
        if(!(e.getDamager() instanceof Skeleton)) {
            return;
        }
        if(!wither_army.contains((Skeleton) e.getDamager())) {
            return;
        }
        if(e.getDamagee().equals(owner)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void transferImageDamage(SmashDamageEvent e) {
        if(!(e.getDamager() instanceof Skeleton)) {
            return;
        }
        if(!wither_army.contains((Skeleton) e.getDamager())) {
            return;
        }
        e.setDamager(owner);
        e.setKnockbackOrigin(e.getDamager().getLocation());
        // Does 10.5 damage on hard mode with the axe, subtract 5.5 to get 5 damage
        e.setDamage(5);
    }

}