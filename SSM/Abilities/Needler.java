package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Needler extends Ability implements OwnerRightClickEvent {

    int needleAmount = 6;

    public Needler() {
        super();
        this.name = "Needler";
        this.cooldownTime = 0;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < needleAmount; i++) {
            Arrow firing = owner.getWorld().spawn(owner.getEyeLocation(), Arrow.class);
            EntityProjectile projectile = new EntityProjectile(plugin, owner.getEyeLocation(), name, firing);
            projectile.setFirer(owner);
            projectile.setDamage(1.0);
            projectile.setSpeed(1.0);
            projectile.setKnockback(0.4);
            projectile.setUpwardKnockback(0.2);
            projectile.setHitboxSize(0.5);
            projectile.setSpread(18);
            projectile.launchProjectile();
        }
    }
}
