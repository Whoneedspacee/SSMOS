package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;

public class Blizzard extends Ability implements OwnerRightClickEvent {

    int BlizzardAmount = 5;

    public Blizzard() {
        super();
        this.name = "Blizzard";
        this.cooldownTime = 0;
        this.expUsed = 0.2F;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < BlizzardAmount; i++) {
            Snowball firing = owner.getWorld().spawn(owner.getEyeLocation(), Snowball.class);
        }
    }
}
