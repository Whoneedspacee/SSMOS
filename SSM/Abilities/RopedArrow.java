package SSM.Abilities;

import SSM.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RopedArrow extends Ability {

    public RopedArrow() {
        this.name = "Roped Arrow";
        this.cooldownTime = 8;
        this.leftClickActivate = true;
    }

    public void useAbility(Player player) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCustomName("Roped Arrow");
        arrow.setDamage(6.0);
        arrow.setVelocity(player.getLocation().getDirection().multiply(1.8D));
    }

    public void pullToArrow(Player player, Arrow arrow) {
        Vector p = player.getLocation().toVector();
        Vector a = arrow.getLocation().toVector();
        Vector pre = a.subtract(p);
        Vector velocity = pre.normalize().multiply(1.8);

        player.setVelocity(new Vector(velocity.getX(), 1, velocity.getZ()));
        arrow.remove();
    }

}
