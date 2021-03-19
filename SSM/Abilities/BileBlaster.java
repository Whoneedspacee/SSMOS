package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BileBlaster extends Ability implements OwnerRightClickEvent {

    private int duration = 65, increment, task;

    public BileBlaster() {
        super();
        this.name = "Bile Blaster";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        increment = 0;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (increment > duration) {
                    Bukkit.getScheduler().cancelTask(task);
                }
                ItemStack bile = new ItemStack(Material.ROTTEN_FLESH, 1);
                Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), bile);
                BileProjectile projectile = new BileProjectile(plugin, owner.getEyeLocation().subtract(0, -1, 0), name, firing);
                projectile.setFirer(owner);
                projectile.launchProjectile();
                increment++;
            }
        }, 0L, 1L);

    }

    class BileProjectile extends EntityProjectile {

        public BileProjectile(Plugin plugin, Location fireLocation, String name, Entity projectile) {
            super(plugin, fireLocation, name, projectile);
            this.setDamage(3.0);
            this.setSpeed(0.45);
            this.setKnockback(0.4);
            this.setUpwardKnockback(0.0);
            this.setHitboxSize(0.3);
            this.setSpread(15);
            this.setFireOpposite(false);
        }
    }
}