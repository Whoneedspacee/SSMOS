package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerAnimationEvent;
import SSM.GameManagers.OwnerEvents.OwnerLeftClickEvent;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class RopedArrow extends Ability implements OwnerLeftClickEvent, OwnerAnimationEvent {

    private Arrow arrow;

    public RopedArrow() {
        super();
        this.name = "Roped Arrow";
        this.cooldownTime = 5;
        this.usage = AbilityUsage.LEFT_CLICK;
        this.useMessage = "You fired";
    }

    public void onOwnerLeftClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void onOwnerAnimation(PlayerAnimationEvent e) {
        checkAndActivate();
    }

    public void activate() {
        arrow = owner.launchProjectile(Arrow.class);
        arrow.setCustomName("Roped Arrow");
        arrow.setMetadata("Roped Arrow", new FixedMetadataValue(plugin, 1));
        arrow.setVelocity(owner.getLocation().getDirection().multiply(2.4));
    }

    @EventHandler
    public void pullToArrow(ProjectileHitEvent e) {
        if (!e.getEntity().equals(arrow)) {
            return;
        }
        Vector p = owner.getLocation().toVector();
        Vector a = arrow.getLocation().toVector();
        Vector pre = a.subtract(p);
        Vector velocity = pre.normalize();
        double mult = (arrow.getVelocity().length() / 3d);

        velocity = velocity.multiply(0.4 + mult);
        velocity.setY(velocity.getY() + 0.6 * mult);

        double y_max = 1.2 * mult;
        if (velocity.getY() > y_max) {
            velocity.setY(y_max);
        }

        if (!owner.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isTransparent()) {
            velocity.setY(velocity.getY() + 0.2);
        }
        owner.setVelocity(velocity);

        arrow.getWorld().playSound(arrow.getLocation(), Sound.ARROW_HIT, 2.5f, 0.5f);
        arrow = null;
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.hasMetadata("Roped Arrow")) {
                    e.setDamage(6.0);
                }
            }
        }
    }
}
