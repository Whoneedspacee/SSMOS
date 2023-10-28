package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import org.bukkit.event.player.PlayerInteractEvent;

public class IronHook extends Ability implements OwnerRightClickEvent {

    public IronHook() {
        super();
        this.name = "Iron Hook";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        IronHookProjectile projectile = new IronHookProjectile(owner, "Iron Hook");
        projectile.launchProjectile();
    }

}