package ssm.abilities;

import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.WhirlpoolProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class WhirlpoolAxe extends Ability implements OwnerRightClickEvent {

    public WhirlpoolAxe() {
        super();
        this.name = "Whirlpool Axe";
        this.cooldownTime = 5;
        this.description = new String[] {
                ChatColor.RESET + "Fires a Prismarine Shard that deals damage to",
                ChatColor.RESET + "the first player it collides with.",
                ChatColor.RESET + "The player is then drawn towards you."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        WhirlpoolProjectile projectile = new WhirlpoolProjectile(owner, name);
        projectile.launchProjectile();
    }

}