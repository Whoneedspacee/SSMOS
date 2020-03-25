package SSM.Abilities;

import SSM.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;

// Uses Shulker Bullet. Very limited as a projectile, not serious.

import java.util.List;

public class Shulker extends Ability {

    private ShulkerBullet Bullet;

    public Shulker() {
        super();
        this.name = "Cast Shulker Bullet";
        this.cooldownTime = 0;
        this.rightClickActivate = true;

    }

    public void activate() {
        Bullet = owner.launchProjectile(ShulkerBullet.class);
        List<Entity> canHit = Bullet.getNearbyEntities(100, 100, 100);
        canHit.remove(Bullet);
        canHit.remove(Bullet.getShooter());
        if (canHit.size() <= 0) {
            return;
        }
        for (Entity entity : canHit) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) canHit.get(0);
            Bullet.setTarget(target);
        }
    }
}