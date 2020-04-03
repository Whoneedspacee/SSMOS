package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class BoneExplosion extends Ability {

    protected int boneAmount = 40;

    public BoneExplosion() {
        super();
        this.name = "Bone Explosion";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_SKELETON_DEATH, 10L, 1L);
        ItemStack bone = new ItemStack(Material.BONE, 1);
        for (int i = 0; i < boneAmount; i++) {
            ItemMeta x = bone.getItemMeta();
            x.setDisplayName("ooga booga"+i);
            bone.setItemMeta(x);
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), bone);
            BoneExplosion.boneProjectile projectile = new BoneExplosion.boneProjectile(plugin, owner, name, firing);
            projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
            projectile.launchProjectile();
            List<Entity> canHit = owner.getNearbyEntities(4, 4, 4);
            canHit.remove(owner);

            if (canHit.size() <= 0) {
                return;
            }

            for (Entity entity : canHit) {
                if ((entity instanceof LivingEntity)) {
                    ((LivingEntity) entity).damage(6.0);
                    Vector p = entity.getLocation().toVector();
                    Vector a = owner.getLocation().toVector();
                    Vector pre = a.subtract(p);
                    Vector velocity = pre.normalize().multiply(1.8);

                    entity.setVelocity(new Vector(velocity.getX()*-.75, 0.4, velocity.getZ()*-.75));
                }
            }
        }

    }

    class boneProjectile extends EntityProjectile {

        public boneProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(0.0);
            this.setSpeed(0.5 + Math.random() * 0.3);
            this.setKnockback(0.0);
            this.setUpwardKnockback(0.0);
            this.setHitboxSize(0.0);
            this.setVariation(360);
            this.setFireOpposite(false);
        }
    }
}