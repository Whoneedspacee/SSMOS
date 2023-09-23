package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class SlimeSlam extends Ability implements OwnerRightClickEvent {

    double damage = 7.0;
    double recoilDamage = 0.5; // Percentage

    public SlimeSlam() {
        this.name = "Slime Slam";
        this.cooldownTime = 6;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
    }

}
