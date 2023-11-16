package ssm.abilities;

import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.WolfProjectile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class CubTackle extends Ability implements OwnerRightClickEvent {

    public static HashMap<Player, Wolf> tackle_wolf = new HashMap<Player, Wolf>();
    public static HashMap<Wolf, Player> tackled_by = new HashMap<Wolf, Player>();

    public CubTackle() {
        super();
        this.name = "Cub Tackle";
        this.cooldownTime = 8;
        this.description = new String[] {
                ChatColor.RESET + "Launch a wolf cub at an opponent.",
                ChatColor.RESET + "If it hits, the cub latches onto the",
                ChatColor.RESET + "opponent, preventing them from moving",
                ChatColor.RESET + "for up to 5 seconds.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        WolfProjectile projectile = new WolfProjectile(owner, name);
        projectile.launchProjectile();
    }

}