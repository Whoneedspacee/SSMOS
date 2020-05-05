package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class InkShotgun extends Ability {

    protected int inkAmount = 7;

    public InkShotgun() {
        super();
        this.name = "Ink Shotgun";
        this.cooldownTime = 6;
        this.rightClickActivate = true;
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 10L, 1L);
        ItemStack ink = new ItemStack(Material.INK_SAC, inkAmount);
        for (int i = 0; i < inkAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), ink);
            InkProjectile projectile = new InkProjectile(plugin, owner, name, firing);
            projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
            projectile.launchProjectile();
        }
    }

    class InkProjectile extends EntityProjectile {

        public InkProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(1.5);
            this.setSpeed(1 + Math.random() * 0.5);
            this.setKnockback(1.0);
            this.setUpwardKnockback(0.5);
            this.setHitboxSize(0.5);
            this.setVariation(15);
            this.setExpAdd(true);
            this.setFireOpposite(false);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            if (target != null) {
                target.setNoDamageTicks(0);
            }
            return super.onHit(target);
        }

    }
}




