package SSM.Abilities;

import SSM.*;
import SSM.Utilities.DamageUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class WolfStrike extends Leap {

    double damage = 7.0;

    public WolfStrike() {
        this.name = "Wolf Strike";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
        this.power = 1.5;
        this.timed = true;
        this.activeTime = 2.5;
        this.endOnLand = true;
        this.hitbox = 1.0;
    }

    public void activate() {
        owner.setVelocity(owner.getLocation().getDirection().multiply(power));
        activity.put(owner.getUniqueId(), true);
        timerList.put(owner.getUniqueId(), System.currentTimeMillis()+(long)activeTime*1000);
    }

    @Override
    public void onHit(LivingEntity target) {
        DamageUtil.dealDamage(owner, target, damage, true, false);
        Vector enemy = target.getLocation().toVector();
        Vector player = owner.getLocation().toVector();
        Vector pre = enemy.subtract(player);
        Vector velocity = pre.normalize().multiply(1.35);
        target.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));

    }


}
