package SSM.Abilities;

import SSM.*;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class SeismicSlam extends Leap {

    private double baseDamage = 9;
    private double range = 6;

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
        this.timed = false;
        this.endOnLand = true;
    }

    public void activate() {
        activity.put(owner.getUniqueId(), true);
        timerList.put(owner.getUniqueId(), System.currentTimeMillis()+(long)activeTime*1000);
        Vector velocity = owner.getVelocity();
        velocity.setY(1.0);
        owner.setVelocity(velocity);

    }

    @Override
    public void onLand() {
        Location loc = owner.getLocation();
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0F, 1.0F);

        List<Entity> canHit = owner.getNearbyEntities(range, range, range);
        canHit.remove(owner);

        for (Entity entity : canHit) {
            if ((entity instanceof LivingEntity)) {
                double dist = loc.distance(entity.getLocation());
                DamageUtil.dealDamage(owner, (LivingEntity) entity, (range - dist) * (baseDamage / range), true, false);
                Vector target = entity.getLocation().toVector();
                Vector player = owner.getLocation().toVector();
                Vector pre = target.subtract(player);
                Vector velocity = pre.normalize().multiply(1.35);
                entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
            }
        }
    }



}