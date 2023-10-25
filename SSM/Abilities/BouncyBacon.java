package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BouncyBacon extends Ability implements OwnerRightClickEvent {

    public BouncyBacon() {
        super();
        this.name = "Bouncy Bacon";
        this.cooldownTime = 0;
        this.expUsed = 0.3f;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        return;
    }

}