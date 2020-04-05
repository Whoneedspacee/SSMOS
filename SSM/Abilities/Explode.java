package SSM.Abilities;

import SSM.Ability;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Explode extends Ability {

    int ExplodeTime = 30;
    int runn = -1;
    int explosionstall = -1;
    int i = 0;

    public Explode() {
        super();
        this.name = "Explode";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate(){
        i = 0;
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 10L, 1L);
        Location location = owner.getLocation();
        Vector direction = location.getDirection();

        explosionstall = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                i++;
                owner.setVelocity(new Vector(0,0,0));
                if (i >= ExplodeTime){
                    stop();
                }
            }
        }, 0,1);
        runn = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 10, 1);
                owner.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, owner.getLocation(), 1);
                owner.setVelocity(owner.getLocation().getDirection().multiply(2));
                List<Entity> canHit = owner.getNearbyEntities(4, 4, 4);
                canHit.remove(owner);

                for (Entity entity : canHit) {
                    if ((entity instanceof LivingEntity)) {
                        ((LivingEntity) entity).damage(6.0);
                        Vector target = entity.getLocation().toVector();
                        Vector player = owner.getLocation().toVector();
                        Vector pre = target.subtract(player);
                        Vector velocity = pre.normalize().multiply(3.50);

                        entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
                    }
                }
            }
}, 30L);
}
    private void stop(){
        Bukkit.getScheduler().cancelTask(explosionstall);
    }
}