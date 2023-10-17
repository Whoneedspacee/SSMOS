package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WolfStrike extends Ability implements OwnerRightClickEvent {

    double damage = 7.0;

    public WolfStrike() {
        this.name = "Wolf Strike";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {

    }

}
