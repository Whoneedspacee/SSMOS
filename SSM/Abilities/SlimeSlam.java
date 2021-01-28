package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Leap;
import SSM.Utilities.DamageUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class SlimeSlam extends Leap implements OwnerRightClickEvent {

    double damage = 7.0;
    double recoilDamage = 0.5; // Percentage

    public SlimeSlam() {
        this.name = "Slime Slam";
        this.cooldownTime = 6;
        this.power = 1.5;
        this.endOnLand = true;
        this.hitbox = 1.0;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        super.init();
        owner.setVelocity(owner.getLocation().getDirection().multiply(power));
    }

    @Override
    public void onHit(LivingEntity target) {
        DamageUtil.dealDamage(owner, target, damage, true, false);
        DamageUtil.dealDamage(owner, damage * recoilDamage);
        Vector enemy = target.getLocation().toVector();
        Vector player = owner.getLocation().toVector();
        Vector pre = enemy.subtract(player);
        Vector velocity = pre.normalize().multiply(1.35);
        target.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));
        owner.setVelocity(new Vector(-velocity.getX(), 0.5, -velocity.getZ()));
    }

    @Override
    public void onLand() {

    }

}
