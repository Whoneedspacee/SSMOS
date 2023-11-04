package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;

public class SulphurBomb extends Ability implements OwnerRightClickEvent {

    /*
     - - Sulphur Bomb
      - - Cooldown
        - '3'
      - - Damage
        - '6.5'
      - - Knockback Magnitude
        - '2.5'
      - - Hit Box
        - '0.65'
     */

    public SulphurBomb() {
        super();
        this.name = "Sulphur Bomb";
        this.cooldownTime = 8;
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
        IronHookProjectile projectile = new IronHookProjectile(owner, name);
        projectile.launchProjectile();
    }

}