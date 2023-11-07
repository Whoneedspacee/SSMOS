package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import SSM.Projectiles.WolfProjectile;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.List;

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