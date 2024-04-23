package xyz.whoneedspacee.ssmos.attributes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;

public class RepellingArcticAura extends ArcticAura {

    public RepellingArcticAura() {
        super();
        this.range = 5;
        this.duration_ms = 10;
    }

    @Override
    public void activate() {
        super.activate();
        double real_range = range * owner.getExp();
        List<Entity> entities = owner.getNearbyEntities(real_range, real_range, real_range);
        for(Entity entity: entities) {
            if(entity instanceof LivingEntity) {
                continue;
            }
            double real_distance = owner.getLocation().distance(entity.getLocation());
            if(real_distance > real_range) {
                continue;
            }
            if(entity.getTicksLived() < 5) {
                continue;
            }
            Vector trajectory = entity.getLocation().subtract(owner.getLocation()).toVector();
            trajectory.normalize();
            trajectory.multiply(0.25 * owner.getExp());
            Vector velocity = entity.getVelocity().clone();
            velocity.multiply(0.5);
            velocity.add(trajectory);
            velocity.setX(Math.max(Math.min(velocity.getX(), 4.0), -4.0));
            velocity.setY(Math.max(Math.min(velocity.getY(), 4.0), -4.0));
            velocity.setZ(Math.max(Math.min(velocity.getZ(), 4.0), -4.0));
            entity.setVelocity(velocity);
        }
    }

}
