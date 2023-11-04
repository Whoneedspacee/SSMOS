package SSM.GameManagers;

import SSM.Attributes.Hunger;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.Disguises.Disguise;
import SSM.Kits.Kit;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class DamageManager implements Listener {

    private static DamageManager ourInstance;
    private JavaPlugin plugin = SSM.getInstance();
    private static List<SmashDamageEvent> damage_record = new ArrayList<>();

    public DamageManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    // Highest priority to get after all changes
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            Bukkit.broadcastMessage(ChatColor.RED + "Error: Cancelled EntityDamageEvent, please use SmashDamageEvent");
            return;
        }
        if (!(e.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damagee = (LivingEntity) e.getEntity();
        LivingEntity damager = null;
        Projectile projectile = null;
        double knockback = 1;
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageBy = (EntityDamageByEntityEvent) e;
            if (damageBy.getDamager() instanceof Projectile) {
                projectile = (Projectile) damageBy.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    damager = (LivingEntity) projectile.getShooter();
                }
            } else if (damageBy.getDamager() instanceof LivingEntity) {
                damager = (LivingEntity) damageBy.getDamager();
            }
        }
        if (e.getCause() == DamageCause.POISON) {
            if (e.getEntity().hasMetadata("Poison Damager")) {
                List<MetadataValue> values = e.getEntity().getMetadata("Poison Damager");
                if (values.size() > 0) {
                    damager = (Player) values.get(0).value();
                    knockback = 0;
                }
            }
        }
        double damage = e.getDamage();
        // Consistent Arrow Damage
        if (projectile != null && projectile instanceof Arrow) {
            damage = projectile.getVelocity().length() * 3;
        }
        // Consistent Melee Damage
        if(damager instanceof Player) {
            Kit kit = KitManager.getPlayerKit((Player) damager);
            if(kit != null) {
                damage = kit.getMelee();
            }
        }
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(damagee, damager, damage);
        smashDamageEvent.multiplyKnockback(knockback);
        smashDamageEvent.setDamageCause(e.getCause());
        smashDamageEvent.setProjectile(projectile);
        smashDamageEvent.callEvent();
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removeArrows(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) e.getEntity();
        arrow.teleport(new Location(arrow.getWorld(), 0, 0, 0));
        arrow.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removeArrows(SmashDamageEvent e) {
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();
        arrow.teleport(new Location(arrow.getWorld(), 0, 0, 0));
        arrow.remove();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void cancelSmashDamage(SmashDamageEvent e) {
        if (e.getDamagee().getHealth() <= 0) {
            e.setCancelled(true);
            return;
        }
        if (e.getDamagee() != null && e.getDamagee() instanceof Player) {
            Player damagee = (Player) e.getDamagee();
            if (damagee.getGameMode() != GameMode.SURVIVAL && damagee.getGameMode() != GameMode.ADVENTURE) {
                e.setCancelled(true);
                return;
            }
            if (GameManager.isSpectator(damagee)) {
                e.setCancelled(true);
                return;
            }
            // Check if the damagee can be hit by the damager again
            if (!e.getIgnoreDamageDelay()) {
                if (!DamageUtil.getDamageRateTracker(damagee).canBeHurtBy(e.getDamager())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (e.getDamager() != null && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (damager.getGameMode() != GameMode.SURVIVAL && damager.getGameMode() != GameMode.ADVENTURE) {
                e.setCancelled(true);
                return;
            }
            // Check if the damager can hit the damagee again
            if (!e.getIgnoreDamageDelay())
                if (!DamageUtil.getDamageRateTracker(damager).canHurt(e.getDamagee())) {
                    e.setCancelled(true);
                    return;
                }
        }
    }

    @EventHandler
    public void borderKill(SmashDamageEvent e) {
        if(e.getDamageCause() != DamageCause.VOID) {
            return;
        }
        if(e.getIgnoreDamageDelay() != false) {
            return;
        }
        if(!(e.getDamagee() instanceof Player)) {
            return;
        }
        DamageUtil.borderKill((Player) e.getDamagee(), true);
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSmashDamage(SmashDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        LivingEntity damagee = e.getDamagee();
        LivingEntity damager = e.getDamager();
        double damage = e.getDamage();
        double knockbackMultiplier = e.getKnockbackMultiplier();
        boolean ignoreArmor = e.getIgnoreArmor();
        DamageCause cause = e.getDamageCause();
        Location origin = e.getKnockbackOrigin();
        String reason = e.getReason();
        Projectile projectile = e.getProjectile();
        if (damagee == null || damage <= 0) {
            e.setCancelled(true);
            return;
        }
        if (!DamageUtil.canDamage(damagee, damager, damage)) {
            e.setCancelled(true);
            return;
        }
        double damageMultiplier = 1;
        double starting_health = damagee.getHealth();
        if (damagee instanceof Player) {
            Player player = (Player) damagee;
            if (KitManager.getPlayerKit((player)) != null) {
                damageMultiplier = 1 - KitManager.getPlayerKit(player).getArmor() * 0.08f;
            }
        }
        if (ignoreArmor) {
            damageMultiplier = 1;
        }
        double previousHealth = damagee.getHealth();
        EntityLiving entityDamagee = ((CraftLivingEntity) damagee).getHandle();
        boolean died = false;
        double new_health = 20;
        double dealt = 0;
        if ((float) damagee.getNoDamageTicks() > (float) damagee.getMaximumNoDamageTicks() / 2.0F) {
            dealt = Math.max(damage - entityDamagee.lastDamage, 0) * damageMultiplier;
        } else {
            dealt = damage * damageMultiplier;
        }
        new_health = Math.max(damagee.getHealth() - dealt, 0);
        entityDamagee.lastDamage = (float) damage;
        // Avoid really killing the player
        if (new_health <= 0) {
            died = true;
        } else {
            damagee.setHealth(new_health);
        }
        storeDamageEvent(e);
        Disguise disguise = DisguiseManager.disguises.get(damagee);
        if (disguise != null) {
            PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus((net.minecraft.server.v1_8_R3.Entity) disguise.getLiving(), (byte) 2);
            Utils.sendPacketToAllBut(disguise.getOwner(), packet);
        }
        damagee.playEffect(EntityEffect.HURT);
        if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE) {
            if (damagee instanceof Player) {
                if (disguise != null) {
                    DamageUtil.playDamageSound(damagee, disguise.getType());
                } else {
                    DamageUtil.playDamageSound(damagee, damagee.getType());
                }
            } else {
                DamageUtil.playDamageSound(damagee, damagee.getType());
            }
        }
        if (damager instanceof Player && cause == DamageCause.PROJECTILE) {
            Player player = (Player) damager;
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 0.5f);
        }
        if (died && !(damagee instanceof Player)) {
            // Actually kill entities
            damagee.damage(100);
        }
        if (died && damagee instanceof Player) {
            Player player = (Player) damagee;
            GameManager.death(player, damager);
        }
        if (damager instanceof Player) {
            Player player = (Player) damager;
            player.setLevel((int) damage);
        }
        if (knockbackMultiplier > 0 && (origin != null || damager != null || projectile != null)) {
            // Don't stack knockback
            VelocityUtil.setVelocity(damagee, new Vector(0, 0, 0));
            double knockback = Math.max(damage, 2);
            knockback = Math.log10(knockback);
            knockback *= knockbackMultiplier;

            if (damagee instanceof Player) {
                Player player = (Player) damagee;
                if (KitManager.getPlayerKit(player) != null) {
                    knockback *= KitManager.getPlayerKit(player).getKnockback();
                }
                knockback *= (1 + 0.1 * (damagee.getMaxHealth() - starting_health));
            }

            //Bukkit.broadcastMessage("HP: " + damagee.getHealth() + " Max: " + damagee.getMaxHealth());
            //Bukkit.broadcastMessage("Damage: " + damage);
            //Bukkit.broadcastMessage("Final KB: " + knockback);

            if (origin == null && damager != null) {
                origin = damager.getLocation();
            }

            Vector trajectory = null;
            if (origin != null) {
                trajectory = damagee.getLocation().toVector().subtract(origin.toVector()).setY(0).normalize();
                trajectory.multiply(0.6 * knockback);
                trajectory.setY(Math.abs(trajectory.getY()));
            }
            if (projectile != null) {
                trajectory = projectile.getVelocity();
                trajectory.setY(0);
                trajectory.multiply(0.37 * knockback / trajectory.length());
                trajectory.setY(0.06);
            }

            double vel = 0.2 + trajectory.length() * 0.8;

            VelocityUtil.setVelocity(damagee, trajectory, vel, false,
                    0, Math.abs(0.2 * knockback), 0.4 + (0.04 * knockback), true);
        }
        if (damagee != damager && damager instanceof Player && damagee instanceof Player) {
            Player player = (Player) damager;
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null) {
                Hunger hunger = (Hunger) kit.getAttributeByName("Hunger");
                if (hunger != null) {
                    hunger.hungerRestore(damage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clearArrow(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            if (e.getHitEntity() != null) {
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

    public static void storeDamageEvent(SmashDamageEvent e) {
        // Condense Damage Records with same variable values except damage and time
        for (int i = 0; i < damage_record.size(); i++) {
            SmashDamageEvent check = damage_record.get(i);
            if (check == null) {
                continue;
            }
            if (e.equals(check)) {
                e.setDamage(e.getDamage() + check.getDamage());
                damage_record.remove(i);
            }
        }
        damage_record.add(e);
    }

    public static void deathReport(Player player) {
        int count = 1;
        List<SmashDamageEvent> to_remove = new ArrayList<SmashDamageEvent>();
        for (int i = damage_record.size() - 1; i >= 0; i--) {
            SmashDamageEvent e = damage_record.get(i);
            // Expunge old records
            if ((System.currentTimeMillis() - e.getDamageTimeMs()) > 15000) {
                continue;
            }
            if (!e.getDamageeName().equals(player.getName())) {
                continue;
            }
            String time_since = String.format("%.1f", (System.currentTimeMillis() - e.getDamageTimeMs()) / 1000.0);
            String damage_amount = "Infinite";
            if (e.getDamage() < 1000) {
                damage_amount = String.format("%.1f", e.getDamage());
            }
            player.sendMessage(ChatColor.DARK_GREEN + "#" + count + ": " +
                    ChatColor.YELLOW + e.getDamagerName() + " " +
                    ChatColor.GRAY + "[" + ChatColor.YELLOW + damage_amount + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + e.getReason() + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + time_since + " Seconds Prior" + ChatColor.GRAY + "]");
            count++;
            to_remove.add(e);
        }
        damage_record.removeAll(to_remove);
    }

    public static SmashDamageEvent getLastDamageEvent(Player player) {
        long last_time = 0;
        SmashDamageEvent record = null;
        for (int i = 0; i < damage_record.size(); i++) {
            SmashDamageEvent check = damage_record.get(i);
            if (!check.getDamageeName().equals(player.getName())) {
                continue;
            }
            if (check.getDamageTimeMs() <= last_time) {
                continue;
            }
            last_time = check.getDamageTimeMs();
            record = check;
        }
        return record;
    }

}
