package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.WebProjectile;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpinWeb extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public SpinWeb() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_IDLE, 2f, 0.6f);
        for (int i = 0; i < webAmount; i++) {
            WebProjectile projectile = new WebProjectile(owner, "Spin Web");
            projectile.launchProjectile();
        }
        VelocityUtil.setVelocity(owner, 1.2, 0.2, 1.2, true);
    }

}
