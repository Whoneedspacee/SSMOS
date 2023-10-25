package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.WebProjectile;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpinWeb extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public SpinWeb() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_IDLE, 2f, 0.6f);
        for (int i = 0; i < webAmount; i++) {
            new WebProjectile(owner, "Spin Web");
        }
        VelocityUtil.setVelocity(owner, 1.2, 0.2, 1.2, true);
    }

}
