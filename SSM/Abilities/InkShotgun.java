package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class InkShotgun extends Ability implements OwnerRightClickEvent {

    protected int inkAmount = 7;

    public InkShotgun() {
        super();
        this.name = "Ink Shotgun";
        this.cooldownTime = 6;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 1.5f, 0.75f);
        owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH, 0.75f, 1f);
        for (int i = 0; i < inkAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation().add(owner.getLocation().getDirection()),
                    new ItemStack(Material.INK_SACK, 1));
            InkProjectile projectile = new InkProjectile(plugin, name, firing);
            projectile.count = i;
            projectile.setFirer(owner);
            projectile.launchProjectile();
        }
    }

    class InkProjectile extends EntityProjectile {

        int smoke_task = -1;
        int count = 0;

        public InkProjectile(Plugin plugin, String name, Entity projectile) {
            super(plugin, name, projectile);
            this.setDamage(1.725);
            this.setKnockback(3.0);
            this.setHitboxSize(0.5);
            this.setResetDamageTicks(true);
            smoke_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Utils.playParticle(EnumParticle.SMOKE_NORMAL, projectile.getLocation(),
                            0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                            null);
                }
            }, 0, 1);
        }

        @Override
        public void doVelocity() {
            Vector random = new Vector(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5);
            random.normalize();
            random.multiply(0.15);
            if (count == 0) {
                random = new Vector(0, 0, 0);
            }
            VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection().add(random),
                    1 + 0.4 * Math.random(), false, 0, 0.2, 10, false);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Bukkit.getScheduler().cancelTask(smoke_task);
            projectile.getWorld().playSound(projectile.getLocation(), Sound.EXPLODE, 0.75f, 1.25f);
            return super.onHit(target);
        }

    }
}




