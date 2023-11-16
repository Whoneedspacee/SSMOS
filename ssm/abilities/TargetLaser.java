package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.CooldownManager;
import ssm.gamemanagers.DisguiseManager;
import ssm.gamemanagers.disguises.Disguise;
import ssm.gamemanagers.disguises.GuardianDisguise;
import ssm.gamemanagers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.gamemanagers.ownerevents.OwnerKillEvent;
import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class TargetLaser extends Ability implements OwnerRightClickEvent, OwnerDealSmashDamageEvent, OwnerKillEvent {

    private int laser_task = -1;
    private Player target = null;
    protected long max_time_ms = 8000;
    protected double max_range = 11;
    protected double damage_increase = 2;
    protected double knockback_increase_multiplier = 1.25;

    public TargetLaser() {
        super();
        this.name = "Target Laser";
        this.cooldownTime = 15;
        this.description = new String[] {
                ChatColor.RESET + "You target the nearest player with your laser.",
                ChatColor.RESET + "That player takes increased damage and knockback from you.",
                ChatColor.RESET + "Your laser breaks if you get too far away or after some time."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (!check()) {
            return;
        }
        if(target != null) {
            return;
        }
        if (!Utils.entityIsOnGround(owner)) {
            Utils.sendAttributeMessage("You must be on the ground to use", name, owner, ServerMessageType.SKILL);
            return;
        }
        double closest_distance = Double.MAX_VALUE;
        for(Player player : Utils.getNearby(owner.getLocation(), max_range + 5)) {
            if(player.equals(owner)) {
                continue;
            }
            if(!DamageUtil.canDamage(player, owner)) {
                continue;
            }
            double distance = player.getLocation().distance(owner.getLocation());
            if(distance > max_range) {
                continue;
            }
            if(distance < closest_distance) {
                target = player;
                closest_distance = distance;
            }
        }
        if(target == null) {
            Utils.sendServerMessageToPlayer("There are no targets within range.", owner, ServerMessageType.SKILL);
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        disguiseTarget(target);
        Utils.sendAttributeMessage("You targeted " + ChatColor.YELLOW + target.getName() +
                ChatColor.GRAY + " with", name, owner, ServerMessageType.GAME);
        Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                ChatColor.GRAY + " targeted you with their", name, target, ServerMessageType.GAME);
        laser_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || target == null) {
                    Bukkit.getScheduler().cancelTask(laser_task);
                    return;
                }
                Utils.playParticle(EnumParticle.CRIT_MAGIC, target.getLocation().add(0, 0.5, 0.5),
                        1f, 0.5f, 1f, 0.1f, 10, 96, target.getWorld().getPlayers());
                long time_elapsed = CooldownManager.getInstance().getTimeElapsedFor(TargetLaser.this, owner);
                if(owner.getLocation().distance(target.getLocation()) > max_range || time_elapsed >= max_time_ms) {
                    long time = time_elapsed / 1000;
                    double damage = 0.5 * time;
                    disguiseTarget(null);
                    Utils.sendAttributeMessage("Your laser broke, dealing damage to",
                            ChatColor.YELLOW + target.getName(), owner, ServerMessageType.GAME);
                    SmashDamageEvent smashDamageEvent = new SmashDamageEvent(target, owner, damage);
                    smashDamageEvent.multiplyKnockback(0);
                    smashDamageEvent.setIgnoreDamageDelay(true);
                    smashDamageEvent.setReason(name);
                    smashDamageEvent.callEvent();
                    target = null;
                    Bukkit.getScheduler().cancelTask(laser_task);
                }
            }
        }, 0L, 5L);
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(target == null) {
            return;
        }
        if(!target.equals(e.getDamagee())) {
            return;
        }
        e.setDamage(e.getDamage() + damage_increase);
        e.multiplyKnockback(knockback_increase_multiplier);
        target.getWorld().playEffect(target.getLocation().add(0, 0.5, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
    }

    @Override
    public void onOwnerKillEvent(Player player) {
        if(target == null) {
            return;
        }
        if(player.equals(target)) {
            applyCooldown(0);
            disguiseTarget(null);
            target = null;
            Bukkit.getScheduler().cancelTask(laser_task);
        }
    }

    public void disguiseTarget(Entity entity) {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof GuardianDisguise)) {
            return;
        }
        GuardianDisguise guardianDisguise = (GuardianDisguise) disguise;
        guardianDisguise.setTarget(entity);
    }

}