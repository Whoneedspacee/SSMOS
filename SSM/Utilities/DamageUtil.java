package SSM.Utilities;

import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.Disguise;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

public class DamageUtil {

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage) {
        damage(damagee, damager, damage, 1.0, false, DamageCause.ENTITY_ATTACK, null);
    }

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage,
                              double knockbackMultiplier, boolean ignoreArmor) {
        damage(damagee, damager, damage, knockbackMultiplier, ignoreArmor, DamageCause.CUSTOM, null);
    }

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage,
                              double knockbackMultiplier, boolean ignoreArmor, DamageCause cause) {
        damage(damagee, damager, damage, knockbackMultiplier, ignoreArmor, cause, null);
    }

    public static void damage(LivingEntity damagee, LivingEntity damager, double damage,
                              double knockbackMultiplier, boolean ignoreArmor, DamageCause cause, Location origin) {
        // Replace disguise being hit with owner
        if (damagee == null) {
            return;
        }
        if (!canDamage(damagee, damage)) {
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
        if ((float) damagee.getNoDamageTicks() > (float) damagee.getMaximumNoDamageTicks() / 2.0F) {
            new_health = Math.max(damagee.getHealth() - (damage - entityDamagee.lastDamage) * damageMultiplier, 0);
        } else {
            new_health = Math.max(damagee.getHealth() - damage * damageMultiplier, 0);
        }
        // Avoid really killing the player
        if (new_health <= 0) {
            died = true;
        } else {
            damagee.setHealth(new_health);
        }
        entityDamagee.lastDamage = (float) damage;
        // Opponent is invincible?
        if (damagee.getHealth() == previousHealth && !died) {
            return;
        }
        damagee.setNoDamageTicks(damagee.getMaximumNoDamageTicks());
        Disguise disguise = DisguiseManager.disguises.get(damagee);
        if (disguise != null) {
            PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus((Entity) disguise.getLiving(), (byte) 2);
            Utils.sendPacketToAllBut(disguise.getOwner(), packet);
        }
        damagee.playEffect(EntityEffect.HURT);
        if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE) {
            if (damagee instanceof Player) {
                if (disguise != null) {
                    playDamageSound(damagee, disguise.getType());
                } else {
                    playDamageSound(damagee, damagee.getType());
                }
            } else {
                playDamageSound(damagee, damagee.getType());
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
            GameManager.death(player);
        }
        if (damager instanceof Player) {
            Player player = (Player) damager;
            player.setLevel((int) damage);
        }
        if (knockbackMultiplier > 0) {
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

            if (origin == null) {
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
        if (resetDamageTicks) {
            damagee.setNoDamageTicks(0);
        }
        damage(damagee, damager, damage, knockbackMultiplier, ignoreArmor, cause, origin);
    }

    public static boolean canDamage(LivingEntity check, double damage) {
        if (GameManager.getState() != GameManager.GameState.GAME_PLAYING) {
            return false;
        }
        EntityLiving entityDamagee = ((CraftLivingEntity) check).getHandle();
        if (check instanceof Player) {
            Player player = (Player) check;
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                return false;
            }
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null && kit.isInvincible()) {
                return false;
            }
        }
        if ((float) check.getNoDamageTicks() > (float) check.getMaximumNoDamageTicks() / 2.0F) {
            if (damage <= entityDamagee.lastDamage) {
                return false;
            }
        }
        return true;
    }

    public static void playDamageSound(LivingEntity damagee, EntityType type) {
        Sound sound = Sound.HURT_FLESH;

        if (type == EntityType.BAT) sound = Sound.BAT_HURT;
        else if (type == EntityType.BLAZE) sound = Sound.BLAZE_HIT;
        else if (type == EntityType.CAVE_SPIDER) sound = Sound.SPIDER_IDLE;
        else if (type == EntityType.CHICKEN) sound = Sound.CHICKEN_HURT;
        else if (type == EntityType.COW) sound = Sound.COW_HURT;
        else if (type == EntityType.CREEPER) sound = Sound.CREEPER_HISS;
        else if (type == EntityType.ENDER_DRAGON) sound = Sound.ENDERDRAGON_GROWL;
        else if (type == EntityType.ENDERMAN) sound = Sound.ENDERMAN_HIT;
        else if (type == EntityType.GHAST) sound = Sound.GHAST_SCREAM;
        else if (type == EntityType.GIANT) sound = Sound.ZOMBIE_HURT;
            //else if (damagee.getType() == EntityType.HORSE)		sound = Sound.
        else if (type == EntityType.IRON_GOLEM) sound = Sound.IRONGOLEM_HIT;
        else if (type == EntityType.MAGMA_CUBE) sound = Sound.MAGMACUBE_JUMP;
        else if (type == EntityType.MUSHROOM_COW) sound = Sound.COW_HURT;
        else if (type == EntityType.OCELOT) sound = Sound.CAT_MEOW;
        else if (type == EntityType.PIG) sound = Sound.PIG_IDLE;
        else if (type == EntityType.PIG_ZOMBIE) sound = Sound.ZOMBIE_HURT;
        else if (type == EntityType.SHEEP) sound = Sound.SHEEP_IDLE;
        else if (type == EntityType.SILVERFISH) sound = Sound.SILVERFISH_HIT;
        else if (type == EntityType.SKELETON) sound = Sound.SKELETON_HURT;
        else if (type == EntityType.SLIME) sound = Sound.SLIME_ATTACK;
        else if (type == EntityType.SNOWMAN) sound = Sound.STEP_SNOW;
        else if (type == EntityType.SPIDER) sound = Sound.SPIDER_IDLE;
            //else if (damagee.getType() == EntityType.SQUID)		sound = Sound;
            //else if (damagee.getType() == EntityType.VILLAGER)	sound = Sound;
            //else if (damagee.getType() == EntityType.WITCH)		sound = Sound.;
        else if (type == EntityType.WITHER) sound = Sound.WITHER_HURT;
        else if (type == EntityType.WOLF) sound = Sound.WOLF_HURT;
        else if (type == EntityType.ZOMBIE) sound = Sound.ZOMBIE_HURT;

        damagee.getWorld().playSound(damagee.getLocation(), sound, 1.5f + (float) (0.5f * Math.random()), 0.8f + (float) (0.4f * Math.random()));
    }

}
