package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class Explode extends Ability implements OwnerRightClickEvent {

    /*
     - - Explode
      - - Cooldown
        - '8'
      - - Warmup (ms)
        - '1500'
      - - Radius Normal
        - '8'
      - - Radius Smash
        - '24'
      - - Damage Normal
        - '20'
      - - Damage Smash
        - '30'
      - - Damage Reduction
        - 100%
      - - Spawn Removal Radius
        - '14'
      - - Knockback Magnitude
        - '2.5'
      - - Block Destroy Radius
        - '12'
      - - Block Regeneration Time
        - '20'
     */

    public Explode() {
        super();
        this.name = "Explode";
        this.cooldownTime = 8;
        this.description = new String[] {
                ChatColor.RESET + "You freeze in location and charge up",
                ChatColor.RESET + "for 1.5 seconds. Then you explode!",
                ChatColor.RESET + "You are sent flying in the direction",
                ChatColor.RESET + "you are looking, while opponents take",
                ChatColor.RESET + "large damage and knockback.",

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