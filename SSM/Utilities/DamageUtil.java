package SSM.Utilities;

import SSM.Attributes.Hunger;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.DamageManager;
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
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class DamageUtil {

    public static void borderKill(Player player, boolean lightning) {
        if(lightning && DamageUtil.canDamage(player, null,1000)) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        if (DamageUtil.canDamage(player, null,1000)) {
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, null, 1000);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreArmor(true);
            smashDamageEvent.setDamageCause(DamageCause.VOID);
            smashDamageEvent.setDamagerName("Void");
            smashDamageEvent.setReason("World Border");
            smashDamageEvent.callEvent();
        }
        player.teleport(player.getWorld().getSpawnLocation());
        if(player.getAllowFlight()) {
            player.setFlying(true);
        }
    }

    public static boolean canDamage(LivingEntity damagee, LivingEntity damager, double damage) {
        if (GameManager.getState() != GameManager.GameState.GAME_PLAYING) {
            return false;
        }
        EntityLiving entityDamagee = ((CraftLivingEntity) damagee).getHandle();
        if (damagee instanceof Player) {
            Player player = (Player) damagee;
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                return false;
            }
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null && kit.isInvincible()) {
                return false;
            }
        }
        if ((float) damagee.getNoDamageTicks() > (float) damagee.getMaximumNoDamageTicks() / 2.0F) {
            if (damage <= entityDamagee.lastDamage) {
                return false;
            }
        }
        return true;
    }

    public static boolean isIntangible(LivingEntity check) {
        if(!(check instanceof Player)) {
            return false;
        }
        Player player = (Player) check;
        Kit kit = KitManager.getPlayerKit(player);
        if(kit == null) {
            return false;
        }
        return kit.isIntangible();
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
