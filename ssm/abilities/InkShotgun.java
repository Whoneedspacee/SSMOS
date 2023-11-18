package ssm.abilities;

import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.InkProjectile;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class InkShotgun extends Ability implements OwnerRightClickEvent {

    protected int inkAmount = 7;

    public InkShotgun() {
        super();
        this.name = "Ink Shotgun";
        this.cooldownTime = 6;
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Blasts 7 ink pellets out at high velocity.",
                ChatColor.RESET + "They explode upon hitting something, dealing",
                ChatColor.RESET + "damage and knockback.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < inkAmount; i++) {
            double spread = 1;
            if(i == 0) {
                spread = 0;
            }
            InkProjectile projectile = new InkProjectile(owner, name, spread);
            projectile.launchProjectile();
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 1.5f, 0.75f);
        owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH, 0.75f, 1f);
    }

}




