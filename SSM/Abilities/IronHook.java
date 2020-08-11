package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        ItemStack hook = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), hook);
        HookProjectile projectile = new HookProjectile(plugin, owner, name, firing);
        projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
        projectile.launchProjectile();
    }

    class HookProjectile extends EntityProjectile {

        int runn = -1;

        public HookProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(6.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setVariation(0);
            this.setKnockback(-2.5);
            this.setUpwardKnockback(0.5);
            runn = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    firer.getWorld().playSound(projectile.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                    firer.getWorld().spawnParticle(Particle.CRIT, projectile.getLocation(), 0);
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