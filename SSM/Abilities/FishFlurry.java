package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.WebProjectile;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FishFlurry extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public FishFlurry() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_IDLE, 2f, 0.6f);
        ItemStack cobweb = new ItemStack(Material.WEB, 1);
        for (int i = 0; i < webAmount; i++) {
            new WebProjectile(owner, "Spin Web");
        }
        VelocityUtil.setVelocity(owner, 1.2, 0.2, 1.2, true);
    }

}
