package xyz.whoneedspacee.ssmos.abilities.original;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.original.SulphurProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
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