package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Leap;
import SSM.Utilities.DamageUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class SeismicSlam extends Leap implements OwnerRightClickEvent {

    private double baseDamage = 9;
    private double range = 6;

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 8;
        this.power = 1;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        super.init();
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

    @Override
    public void onHit(LivingEntity target) {

    }


}