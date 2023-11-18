package ssm.abilities;

import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.SulphurProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class SulphurBomb extends Ability implements OwnerRightClickEvent {

    public SulphurBomb() {
        super();
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
        this.description = new String[] {
                ChatColor.RESET + "Throw a small bomb of sulphur.",
                ChatColor.RESET + "Explodes on contact with players,",
                ChatColor.RESET + "dealing some damage and knockback.",

        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        SulphurProjectile projectile = new SulphurProjectile(owner, name);
        projectile.launchProjectile();
    }

}