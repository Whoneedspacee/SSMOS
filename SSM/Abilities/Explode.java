package SSM.Abilities;

import SSM.Ability;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class Explode extends Ability {

    private double timeToExplode = 1.5; //Seconds
    private long time;
    private int task;
    private int range = 5;
    private int baseDamage = 15;

    public Explode() {
        super();
        this.name = "Explode";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 2);
        time = System.currentTimeMillis() + (int) (timeToExplode * 1000);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (owner.isSneaking()) {
                    Bukkit.getScheduler().cancelTask(task);
                }
                owner.setVelocity(new Vector(0, 0, 0));
                if (System.currentTimeMillis() >= time) {
                    Bukkit.getScheduler().cancelTask(task);
                    explode();
                }
            }
        }, 0L, 1L);
    }

    private void explode() {
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
            DamageUtil.dealDamage(owner, target, (range - dist) * (double) (baseDamage / range), true, false);
        }
    }
}

