package SSM.Abilities;

import SSM.*;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class SeismicSlam extends Ability {

    boolean active = false;
    int i = 0;
    int runn = -1;

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 2;
        this.rightClickActivate = true;
    }

    public void activate() {
        active = false;
        i = 0;
        Vector velocity = owner.getVelocity();
        velocity.setY(0.8);
        owner.setVelocity(velocity);
        runn = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                i++;
                if (i >= 15) {
                    active = true;
                }
                if (active && owner.isOnGround()){
                    Bukkit.getScheduler().cancelTask(runn);
                    land();
                }
            }
        }, 1, 1);
    }

    public void land() {
        active = false;
        owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_STONE_BREAK, 10, 1);
        List<Entity> canHit = owner.getNearbyEntities(6, 4, 6);
        canHit.remove(owner);

        for (Entity entity : canHit) {
            if ((entity instanceof LivingEntity)) {
                ((LivingEntity) entity).damage(6.0);
                Vector target = entity.getLocation().toVector();
                Vector player = owner.getLocation().toVector();
                Vector pre = target.subtract(player);
                Vector velocity = pre.normalize().multiply(1.35);

                entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
            }
        }
    }
}