package xyz.whoneedspacee.ssmos.abilities.original;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.original.WhirlpoolProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
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