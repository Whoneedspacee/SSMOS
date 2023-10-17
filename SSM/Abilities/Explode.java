package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Explode extends Ability implements OwnerRightClickEvent {

    private double timeToExplode = 1.5; //Seconds
    private int task;
    private int range = 5;
    private int baseDamage = 15;

    public Explode() {
        super();
        this.name = "Explode";
        this.cooldownTime = 8;
        this.useMessage = "You are charging";
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            long start_time = System.currentTimeMillis();
            long explode_time = start_time + (long) (timeToExplode * 1000);

            @Override
            public void run() {
                if (owner.isSneaking()) {
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                float elapsed_time = (float) ((System.currentTimeMillis() - start_time) / 1000.0);
                owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_HISS, 0.5f + elapsed_time, 0.5f + elapsed_time);
                owner.setVelocity(new Vector(0, 0, 0));
                // Disguise Stuff

                if (System.currentTimeMillis() >= explode_time) {
                    Bukkit.getScheduler().cancelTask(task);
                    explode();
                }
            }
        }, 0L, 1L);
    }

    private void explode() {
        Utils.playParticle(EnumParticle.EXPLOSION_HUGE, owner.getLocation(), 0, 0, 0, 0, 1,
                128, null);
        owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 2f, 1f);
        owner.setVelocity(owner.getLocation().getDirection().normalize().multiply(2.0));
        List<Entity> canHit = owner.getNearbyEntities(range, range, range);
        canHit.remove(owner);
        for (Entity entity : canHit) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) entity;
            Location playerLoc = owner.getLocation();
            Location targetLoc = target.getLocation();
            double dist = playerLoc.distance(targetLoc);
            DamageUtil.damage(target, owner, (range - dist) * (double) (baseDamage / range), 2.5f, false);
        }
    }
}

