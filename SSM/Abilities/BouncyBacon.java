package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class BouncyBacon extends Ability implements OwnerRightClickEvent {

    public BouncyBacon() {
        super();
        this.name = "Bouncy Bacon";
        this.cooldownTime = 0;
        this.usesEnergy = true;
        this.expUsed = 0.3f;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        ItemStack pork = new ItemStack(Material.PORKCHOP, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), pork);
        PorkProjectile projectile = new PorkProjectile(plugin, owner, name, firing);
        projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
        projectile.launchProjectile();
    }

    class PorkProjectile extends EntityProjectile {

        public PorkProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(4.0);
            this.setSpeed(1.8);
            this.setHitboxSize(0.6);
            this.setVariation(0);
            this.setPierce(true);
            this.setKnockback(-1.0);
            this.setUpwardKnockback(0.5);
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