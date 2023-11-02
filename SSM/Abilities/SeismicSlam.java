package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.BlocksUtil;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class SeismicSlam extends Ability implements OwnerRightClickEvent {

    private double baseDamage = 11;
    private double range = 8;
    private int task = -1;

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 10;
        this.description = new String[] {
                ChatColor.RESET + "Begin an earthquake that will give damage",
                ChatColor.RESET + "and knockback to any player who is touching",
                ChatColor.RESET + "the ground, anywhere on the map!",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Location loc = owner.getLocation();
        Vector direction = loc.getDirection();
        direction.setY(Math.abs(direction.getY()));

        VelocityUtil.setVelocity(owner, direction, 1, true, 1, 0, 1, true);

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (owner.isDead()) {
                    Bukkit.getScheduler().cancelTask(task);
                }
                if (Utils.entityIsOnGround(owner)) {
                    Bukkit.getScheduler().cancelTask(task);
                    doSlam();
                }
            }
        }, 20L, 0L);
    }

    public void doSlam() {
        List<Entity> canHit = owner.getNearbyEntities(range, range, range);
        canHit.remove(owner);
        for (Entity entity : canHit) {
            if ((entity instanceof LivingEntity)) {
                LivingEntity livingEntity = (LivingEntity) entity;
                double dist = owner.getLocation().distance(entity.getLocation());
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, baseDamage * (1 - dist / range) + 0.5);
                smashDamageEvent.multiplyKnockback(2.4);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
            }
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_WOOD, 2f, 0.2f);
        for (Block check : BlocksUtil.getBlocks(owner.getLocation(), 4)) {
            if (check.getType().isSolid() && !check.getRelative(BlockFace.UP).getType().isSolid()) {
                if (Math.random() < 0.2) {
                    check.getWorld().playEffect(check.getLocation(), Effect.STEP_SOUND, check.getTypeId());
                }
            }
        }
    }

}