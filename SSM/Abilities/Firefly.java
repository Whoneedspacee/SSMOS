package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Firefly extends Ability {

    int stall = -1;
    int runn = -1;
    int i = 0;
    double fireflyTime = 0;

    public Firefly() {
        super();
        this.name = "Firefly";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate(){
        fireflyTime = 0;
        i = 0;
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 0.6F);

        stall = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                i++;
                owner.setLastDamage(0.0);
                owner.setVelocity(new Vector(0,0,0));
                owner.getWorld().spawnParticle(Particle.FLAME, owner.getLocation(),1);
                if (i >= 15){
                    Bukkit.getScheduler().cancelTask(stall);
                    i = 0;
                }
            }
        },0,1);
        runn = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (fireflyTime <= 40) {
                    owner.setInvulnerable(true);
                    fireflyTime++;
                    owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.6F, 1.2F);
                    owner.setVelocity((owner.getLocation().getDirection().multiply(0.7D).add(new Vector(0.0D, 0.1D, 0.0D))));
                    owner.getWorld().spawnParticle(Particle.FLAME, owner.getLocation(), 10);
                    List<Entity> canHit = owner.getNearbyEntities(2, 2, 2);
                    canHit.remove(owner);

                    for (Entity entity : canHit) {
                        if (entity instanceof LivingEntity) {
                            entity.playEffect(EntityEffect.HURT);
                        }
                    }
                    List<Entity> hardHit = owner.getNearbyEntities(1, 1, 1);
                    hardHit.remove(owner);

                    for (Entity entity : hardHit) {
                        if (entity instanceof LivingEntity) {
                            ((LivingEntity) entity).damage(6);
                            Vector target = entity.getLocation().toVector();
                            Vector player = owner.getLocation().toVector();
                            Vector pre = target.subtract(player);
                            Vector velocity = pre.normalize().multiply(1.85);
                            entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
                        }
                    }
                    if (fireflyTime >= 40){
                        owner.setInvulnerable(false);
                        Bukkit.getScheduler().cancelTask(runn);
                    }
                }
            }
        },15,1);
    }

    @EventHandler
    public void onHit(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            if (e.getDamage() >= 2.0){
                Bukkit.getScheduler().cancelTask(stall);
                Bukkit.getScheduler().cancelTask(runn);
            }
        }
    }
}
