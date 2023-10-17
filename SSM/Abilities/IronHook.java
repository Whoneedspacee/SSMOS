package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

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
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation().add(owner.getLocation().getDirection()), hook);
        HookProjectile projectile = new HookProjectile(plugin, name, firing);
        firing.getWorld().playSound(firing.getLocation(), Sound.IRONGOLEM_THROW, 2f, 0.8f);
        projectile.setFirer(owner);
        projectile.launchProjectile();
    }

    class HookProjectile extends EntityProjectile {

        int hook_task = -1;

        public HookProjectile(Plugin plugin, String name, Entity projectile) {
            super(plugin, name, projectile);
            this.setDamage(6.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setResetDamageTicks(true);
            hook_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    firer.getWorld().playSound(projectile.getLocation(), Sound.FIRE_IGNITE, 1.4f, 0.8f);
                    Utils.playParticle(EnumParticle.CRIT, projectile.getLocation(),
                            0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                            null);
                }
            }, 0, 1);
        }

        @Override
        public void doVelocity() {
            VelocityUtil.setVelocity(projectile, owner.getLocation().getDirection(),
                    1.8, false, 0, 0.2, 10, false);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Bukkit.getScheduler().cancelTask(hook_task);
            this.setDamage(projectile.getVelocity().length() * 4);
            return super.onHit(target);
        }

        @Override
        public void doKnockback(LivingEntity target) {
            // To - From
            Vector pull = firer.getLocation().toVector().subtract(target.getLocation().toVector()).normalize();
            VelocityUtil.setVelocity(target,
                    pull, 2, false, 0, 0.8, 1.5, true);
        }
    }
}