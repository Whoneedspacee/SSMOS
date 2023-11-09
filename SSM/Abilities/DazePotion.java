package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import SSM.Projectiles.PotionProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class DazePotion extends Ability implements OwnerRightClickEvent {

    public DazePotion() {
        super();
        this.name = "Daze Potion";
        this.cooldownTime = 2;
        this.description = new String[] {
                ChatColor.RESET + "Throw a potion that damages and slows",
                ChatColor.RESET + "anything it splashes onto!",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        PotionProjectile projectile = new PotionProjectile(owner, name);
        projectile.launchProjectile();
    }

}