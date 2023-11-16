package ssm.abilities;

import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.WebProjectile;
import ssm.utilities.VelocityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class SpinWeb extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public SpinWeb() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 10;
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Spray out webs behind you, launching",
                ChatColor.RESET + "yourself forwards. Webs will damage",
                ChatColor.RESET + "opponents and spawn temporary web blocks.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_IDLE, 2f, 0.6f);
        for (int i = 0; i < webAmount; i++) {
            WebProjectile projectile = new WebProjectile(owner, name);
            projectile.launchProjectile();
        }
        VelocityUtil.setVelocity(owner, 1.2, 0.2, 1.2, true);
    }

}
