package SSM.Abilities;

import SSM.Ability;
import SSM.GameManagers.OwnerEvents.OwnerLeftClickEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class RopedArrow extends Ability implements OwnerLeftClickEvent {

    private Arrow arrow;

    public RopedArrow() {
        super();
        this.name = "Roped Arrow";
        this.cooldownTime = 8;
    }

    public void onOwnerLeftClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        arrow = owner.launchProjectile(Arrow.class);
        arrow.setCustomName("Roped Arrow");
        arrow.setDamage(4.0);
        arrow.setVelocity(owner.getLocation().getDirection().multiply(1.8D));
    }

    @EventHandler
    public void pullToArrow(ProjectileHitEvent e) {
        if (e.getEntity() != arrow) {
            return;
        }
        Vector p = owner.getLocation().toVector();
        Vector a = arrow.getLocation().toVector();
        Vector pre = a.subtract(p);
        Vector velocity = pre.normalize().multiply(1.8);

        owner.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));
        arrow.remove();
    }

}
