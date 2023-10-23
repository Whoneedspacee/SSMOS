package SSM.GameManagers;

import SSM.Kits.Kit;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DamageManager implements Listener {

    public static class DamageRecord {
        private String damagee_name;
        private String damager_name;
        private String damage_reason;
        private double damage_amount;
        private long damage_time;


        public DamageRecord(String damagee_name, String damager_name, double damage_amount, String damage_reason) {
            this.damagee_name = damagee_name;
            this.damager_name = damager_name;
            this.damage_reason = damage_reason;
            this.damage_amount = damage_amount;
            damage_time = System.currentTimeMillis();
        }

        public double getTimeSince() {
            double time = (System.currentTimeMillis() - damage_time) / 1000.0;
            time = Math.round(time * 10) / 10;
            return time;
        }

        public String getDamagerName() {
            return damager_name;
        }

        public String getDamageeName() {
            return damagee_name;
        }

        public String getDamageReason() {
            return damage_reason;
        }

        public String getDamageAmount() {
            if(damage_amount >= 1000) {
                return "Infinite";
            }
            return (Math.round(damage_amount * 10) / 10) + "";
        }

        public long getDamageTime() { return damage_time; }

        public boolean equals(DamageRecord record) {
            return getDamagerName().equals(record.getDamagerName()) &&
                    getDamageeName().equals(record.getDamageeName()) &&
                    getDamageReason().equals(record.getDamageReason());
        }

    }

    private static DamageManager ourInstance;
    private JavaPlugin plugin = SSM.getInstance();
    private static List<DamageRecord> damage_record = new ArrayList<>();

    public DamageManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static void deathReport(Player player) {
        int count = 1;
        for(int i = damage_record.size() - 1; i >= 0; i--) {
            DamageRecord record = damage_record.get(i);
            // Expunge old records
            if(record.getTimeSince() > 15) {
                continue;
            }
            if(!record.getDamageeName().equals(player.getName())) {
                continue;
            }
            player.sendMessage(ChatColor.DARK_GREEN + "#" + count + ": " +
                    ChatColor.YELLOW + record.getDamagerName() + " " +
                    ChatColor.GRAY + "[" + ChatColor.YELLOW + record.getDamageAmount() + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + record.getDamageReason() + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + record.getTimeSince() + " Seconds Prior" + ChatColor.GRAY + "]");
            count++;
            damage_record.remove(i);
        }
    }

    public static DamageRecord getLastDamageRecord(Player player) {
        long last_time = 0;
        DamageRecord record = null;
        for(int i = 0; i < damage_record.size(); i++) {
            DamageRecord check = damage_record.get(i);
            if(!check.getDamageeName().equals(player.getName())) {
                continue;
            }
            if(check.getDamageTime() <= last_time) {
                continue;
            }
            last_time = check.getDamageTime();
            record = check;
        }
        return record;
    }

    public static void addDamageRecord(DamageRecord record) {
        // Condense Damage Records with same variable values except damage and time
        for(int i = 0; i < damage_record.size(); i++) {
            DamageRecord check = damage_record.get(i);
            if(check == null || record == null) {
                continue;
            }
            if(check.equals(record)) {
                record.damage_amount += check.damage_amount;
                damage_record.remove(i);
            }
        }
        damage_record.add(record);
    }

    // High priority to handle all damage events after all additions
    @EventHandler(priority = EventPriority.HIGHEST)
    public void customDamage(EntityDamageByEntityEvent e) {
        // Something else didn't want this to happen and we are going to listen
        if (e.isCancelled()) {
            return;
        }
        // Cancel the event since we're going to handle all the information
        Entity entity = e.getDamager();
        e.setCancelled(true);
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Kit playerKit = KitManager.getPlayerKit(player);
            if (playerKit == null) {
                return;
            }
            if (e.getEntity() instanceof LivingEntity && e.getDamager() instanceof LivingEntity) {
                DamageUtil.damage((LivingEntity) e.getEntity(), (LivingEntity) e.getDamager(), playerKit.getMelee(),
                        1.0, false, DamageCause.ENTITY_ATTACK, null, "Attack");
            }
        } else if (entity instanceof Projectile) {
            // We are technically checking this twice but this is easier to avoid stuff like arrow dings
            if (!DamageUtil.canDamage((LivingEntity) e.getEntity(), e.getDamage())) {
                return;
            }
            Projectile projectile = (Projectile) entity;
            LivingEntity livingEntity = null;
            if (projectile.getShooter() instanceof LivingEntity) {
                livingEntity = (LivingEntity) projectile.getShooter();
            }
            String reason = projectile.getCustomName();
            if(reason == null) {
                reason = "Arrow";
            }
            DamageUtil.damage((LivingEntity) e.getEntity(), livingEntity, e.getDamage(),
                    1.0, false, DamageCause.PROJECTILE,
                    e.getEntity().getLocation().subtract(projectile.getVelocity()), reason);
        } else if (e.getCause() == DamageCause.ENTITY_ATTACK) {
            DamageUtil.damage((LivingEntity) e.getEntity(), (LivingEntity) e.getDamager(), e.getDamage(),
                    1.0, false, DamageCause.ENTITY_ATTACK, null, "Attack");
        } else if (e.getCause() == DamageCause.ENTITY_EXPLOSION) {
            DamageUtil.damage((LivingEntity) e.getEntity(), (LivingEntity) e.getDamager(), e.getDamage(),
                    1.0, false, DamageCause.ENTITY_EXPLOSION);
        } else {
            Bukkit.broadcastMessage("Unhandled Cause: " + e.getCause().toString());
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

}
