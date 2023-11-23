package ssm.utilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.GameManager;
import ssm.managers.KitManager;
import ssm.managers.TeamManager;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;
import ssm.managers.teams.SmashTeam;
import ssm.kits.Kit;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashMap;

public class DamageUtil {

    private static HashMap<LivingEntity, DamageRateTracker> damageRateTrackers = new HashMap<LivingEntity, DamageRateTracker>();

    public static void borderKill(Player player, boolean lightning) {
        if(lightning && DamageUtil.canDamage(player, null)) {
            player.getWorld().strikeLightningEffect(player.getLocation());
        }
        if (DamageUtil.canDamage(player, null)) {
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, null, 1000);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreArmor(true);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setDamageCause(DamageCause.VOID);
            smashDamageEvent.setDamagerName("Void");
            smashDamageEvent.setReason("World Border");
            smashDamageEvent.callEvent();
        }
        player.teleport(player.getWorld().getSpawnLocation());
    }

    public static boolean canDamage(LivingEntity damagee, LivingEntity damager) {
        if(damagee.equals(damager)) {
            return true;
        }
        if (damagee instanceof Player) {
            Player player = (Player) damagee;
            if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                return false;
            }
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null && kit.isInvincible()) {
                return false;
            }
            if(damager instanceof Player) {
                Player player_damager = (Player) damager;
                SmashTeam damagee_team = TeamManager.getPlayerTeam(player);
                SmashTeam damager_team = TeamManager.getPlayerTeam(player_damager);
                if(damagee_team != null && damagee_team.equals(damager_team)) {
                    return false;
                }
            }
            SmashServer server = GameManager.getPlayerServer(player);
            if(server != null) {
                return (server.getState() == GameState.GAME_PLAYING);
            } else {
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

    public static DamageRateTracker getDamageRateTracker(LivingEntity living) {
        DamageRateTracker tracker = damageRateTrackers.get(living);
        if(tracker == null) {
            tracker = new DamageRateTracker(living);
            damageRateTrackers.put(living, tracker);
        }
        return tracker;
    }

    public static class DamageRateTracker {

        private LivingEntity living;
        private HashMap<LivingEntity, Long> lastHurt = new HashMap<>();
        private HashMap<LivingEntity, Long> lastHurtBy = new HashMap<>();
        private long lastHurtByWorld = 0;

        public DamageRateTracker(LivingEntity living) {
            this.living = living;
        }

        public boolean canBeHurtBy(LivingEntity damager) {
            if (damager == null) {
                if (System.currentTimeMillis() - lastHurtByWorld > 250) {
                    lastHurtByWorld = System.currentTimeMillis();
                    return true;
                }
                return false;
            }
            if (!lastHurtBy.containsKey(damager)) {
                lastHurtBy.put(damager, System.currentTimeMillis());
                return true;
            }
            if (System.currentTimeMillis() - lastHurtBy.get(damager) > 400) {
                lastHurtBy.put(damager, System.currentTimeMillis());
                return true;
            }
            return false;
        }

        public boolean canHurt(LivingEntity damagee) {
            if (damagee == null) {
                return true;
            }
            if (!lastHurt.containsKey(damagee)) {
                lastHurt.put(damagee, System.currentTimeMillis());
                return true;
            }
            if (System.currentTimeMillis() - lastHurt.get(damagee) > 400) {
                lastHurt.put(damagee, System.currentTimeMillis());
                return true;
            }
            return false;
        }

    }

}
