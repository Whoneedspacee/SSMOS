package ssm.abilities;

import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.IronHookProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class IronHook extends Ability implements OwnerRightClickEvent {

    public IronHook() {
        super();
        this.name = "Iron Hook";
        this.cooldownTime = 8;
        this.description = new String[] {
                ChatColor.RESET + "Throw a metal hook at opponents.",
                ChatColor.RESET + "If it hits, it deals damage and pulls",
                ChatColor.RESET + "them towards you with great force.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        IronHookProjectile projectile = new IronHookProjectile(owner, name);
        projectile.launchProjectile();
    }

}