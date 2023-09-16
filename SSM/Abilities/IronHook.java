package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class IronHook extends Ability implements OwnerRightClickEvent {

    public IronHook() {
        super();
        this.name = "Iron Hook";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        ItemStack hook = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), hook);
        HookProjectile projectile = new HookProjectile(plugin, owner.getEyeLocation().subtract(0, -1, 0), name, firing);
        projectile.setFirer(owner);
        projectile.launchProjectile();
    }

    class HookProjectile extends EntityProjectile {

        int runn = -1;

        public HookProjectile(Plugin plugin, Location fireLocation, String name, Entity projectile) {
            super(plugin, fireLocation, name, projectile);
            this.setDamage(6.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setSpread(0);
            this.setKnockback(-2.5);
            this.setUpwardKnockback(0.5);
            runn = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    firer.getWorld().playSound(projectile.getLocation(), Sound.FIRE_IGNITE, 1, 1);
                    firer.getWorld().playEffect(projectile.getLocation(), Effect.CRIT, 0);
                }
            }, 0, 1);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Bukkit.getScheduler().cancelTask(runn);
            return super.onHit(target);
        }
    }
}