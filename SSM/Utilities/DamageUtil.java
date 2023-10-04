package SSM.Utilities;

import SSM.GameManagers.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class DamageUtil {

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage) {
        damage(damagee, damager, damage, 1.0, false, DamageCause.ENTITY_ATTACK, null);
    }

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage,
                              double knockbackMultiplier, boolean ignoreArmor, DamageCause cause, Location origin) {
        if (damagee == null) {
            return;
        }
        if (damagee.getHealth() <= 0) {
            return;
        }
        if(!canDamage(damagee)) {
            return;
        }
        double damageMultiplier = 1;
        if(damagee instanceof Player) {
            Player player = (Player) damagee;
            if(KitManager.getPlayerKit((player)) != null) {
                damageMultiplier = 1 - KitManager.getPlayerKit(player).getArmor() * 0.08f;
            }
        }
        if(ignoreArmor) {
            damageMultiplier = 1;
        }
        double previousHealth = damagee.getHealth();
        damagee.damage(damage * damageMultiplier);
        // Something cancelled our damage probably, so let's just pretend we never got hit
        if(damagee.getHealth() == previousHealth) {
            return;
        }
        damagee.setNoDamageTicks(damagee.getMaximumNoDamageTicks());
        damagee.playEffect(EntityEffect.HURT);
        if (damager instanceof Player)
        {
            Player player = (Player) damager;
            player.setLevel((int)damage);
        }
        if (knockbackMultiplier > 0)
        {
            // Don't stack knockback
            VelocityUtil.setVelocity(damagee, new Vector(0, 0, 0));
            double knockback = Math.max(damage, 2);
            knockback = Math.log10(knockback);
            knockback *= knockbackMultiplier;

            if(damagee instanceof Player) {
                Player player = (Player) damagee;
                if(KitManager.getPlayerKit(player) != null) {
                    knockback *= KitManager.getPlayerKit(player).getKnockback();
                }
                knockback *= (1 + 0.1 * (20 - damagee.getHealth()));
            }

            //Bukkit.broadcastMessage("HP: " + damagee.getHealth());
            //Bukkit.broadcastMessage("Damage: " + damage);
            //Bukkit.broadcastMessage("Final KB: " + knockback);

            if(origin == null) {
                origin = damager.getLocation();
            }

            Vector trajectory = damagee.getLocation().toVector().subtract(origin.toVector()).setY(0).normalize();
            trajectory.multiply(0.6 * knockback);
            trajectory.setY(Math.abs(trajectory.getY()));

            double vel = 0.2 + trajectory.length() * 0.8;

            VelocityUtil.setVelocity(damagee, trajectory, vel, false,
                    0, Math.abs(0.2 * knockback), 0.4 + (0.04 * knockback), true);
        }

    }

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage,
                              double knockbackMultiplier, boolean ignoreArmor, DamageCause cause, Location origin, boolean resetDamageTicks) {
        if(resetDamageTicks) {
            damagee.setNoDamageTicks(0);
        }
        damage(damagee, damager, damage, knockbackMultiplier, ignoreArmor, cause, origin);
    }

    public static boolean canDamage(LivingEntity check) {
        if(check instanceof Player) {
            Player player = (Player) check;
            if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                return false;
            }
        }
        if ((float) check.getNoDamageTicks() > (float) check.getMaximumNoDamageTicks() / 2.0F)
        {
            return false;
        }
        return true;
    }

}
