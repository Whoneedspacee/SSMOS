package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class ChickenMissile extends Ability implements OwnerRightClickEvent {

    protected double damage = 8;
    protected double explosion_radius = 3;
    protected double direct_hitbox_radius = 2;
    protected long minimum_hit_time_ms = 200;
    protected long duration_ms = 4000;

    public ChickenMissile() {
        super();
        this.name = "Chicken Missile";
        this.cooldownTime = 7;
        this.description = new String[]{
                ChatColor.RESET + "Launch one of your newborn babies.",
                ChatColor.RESET + "It will fly forwards and explode if it",
                ChatColor.RESET + "collides with anything, giving large",
                ChatColor.RESET + "damage and knockback to players.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Chicken chicken = owner.getWorld().spawn(owner.getEyeLocation().add(owner.getLocation().getDirection()), Chicken.class);
        chicken.getLocation().setPitch(0);
        chicken.getLocation().setYaw(owner.getLocation().getYaw());
        chicken.setBaby();
        chicken.setAgeLock(true);
        Vector direction = owner.getLocation().getDirection().multiply(0.6);
        BukkitRunnable runnable = new BukkitRunnable() {
            private Location last_location = null;

            @Override
            public void run() {
                if (owner == null) {
                    cancel();
                    return;
                }
                chicken.setVelocity(direction);
                chicken.getWorld().playSound(chicken.getLocation(), Sound.CHICKEN_HURT, 0.3f, 1.5f);
                if (CooldownManager.getInstance().getTimeElapsedFor(ChickenMissile.this, owner) < minimum_hit_time_ms) {
                    return;
                }
                boolean detonate = false;
                if (CooldownManager.getInstance().getTimeElapsedFor(ChickenMissile.this, owner) >= duration_ms) {
                    detonate = true;
                } else {
                    HashMap<LivingEntity, Double> targets = Utils.getInRadius(chicken.getLocation(), direct_hitbox_radius);
                    for(LivingEntity livingEntity : targets.keySet()) {
                        if(livingEntity.equals(owner) || livingEntity.equals(chicken)) {
                            continue;
                        }
                        if(!DamageUtil.canDamage(livingEntity, owner)) {
                            continue;
                        }
                        ChickenMissile.this.applyCooldown(0);
                        detonate = true;
                        break;
                    }
                    if(last_location != null && last_location.distance(chicken.getLocation()) < 0.2 || Utils.entityIsOnGround(chicken)) {
                        detonate = true;
                    }
                    last_location = chicken.getLocation();
                }
                if(detonate) {
                    HashMap<LivingEntity, Double> targets = Utils.getInRadius(chicken.getLocation(), explosion_radius);
                    for(LivingEntity livingEntity : targets.keySet()) {
                        if(livingEntity.equals(owner) || livingEntity.equals(chicken)) {
                            continue;
                        }
                        if(!DamageUtil.canDamage(livingEntity, owner)) {
                            continue;
                        }
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage);
                        smashDamageEvent.multiplyKnockback(0);
                        smashDamageEvent.setIgnoreDamageDelay(true);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                        Vector living2d = livingEntity.getLocation().toVector().setY(0);
                        Vector chicken2d = chicken.getLocation().toVector().setY(0);
                        VelocityUtil.setVelocity(livingEntity, living2d.clone().subtract(chicken2d).normalize(),
                                1.6, true, 0.8, 0, 10, true);
                    }
                    Utils.playParticle(EnumParticle.EXPLOSION_HUGE, chicken.getLocation(),
                            0, 0, 0, 0,1, 96, chicken.getWorld().getPlayers());
                    chicken.getWorld().playSound(chicken.getLocation(), Sound.EXPLODE, 2f, 1.2f);
                    Utils.playFirework(chicken.getLocation().add(0, 0.6, 0), FireworkEffect.Type.BALL, Color.WHITE, false, false);
                    chicken.remove();
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 0L, 0L);
    }

}