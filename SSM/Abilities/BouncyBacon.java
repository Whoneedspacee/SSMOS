package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Location;
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
        ItemStack pork = new ItemStack(Material.PORK, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation().subtract(0, -1, 0), pork);
        PorkProjectile projectile = new PorkProjectile(plugin, name, firing);
        projectile.setFirer(owner);
        projectile.launchProjectile();
    }

    class PorkProjectile extends EntityProjectile {

        public PorkProjectile(Plugin plugin, String name, Entity projectile) {
            super(plugin, name, projectile);
            this.setDamage(4.0);
            this.setSpeed(1.8);
            this.setHitboxSize(0.6);
            this.setSpread(0);
            this.setPierce(true);
            this.setKnockback(-1.0);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Vector p = owner.getLocation().toVector();
            Vector proj = projectile.getLocation().toVector();
            Vector pre = p.subtract(proj);
            Vector velocity = pre.normalize().multiply(1.35);
            projectile.setVelocity(velocity.setY(0.4));
            return super.onHit(target);
        }
    }
}