package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

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
        owner.getWorld().playSound(owner.getLocation(), Sound.GHAST_FIREBALL, 10L, 1L);
        ItemStack ink = new ItemStack(Material.INK_SACK, inkAmount);
        for (int i = 0; i < inkAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), ink);
            InkProjectile projectile = new InkProjectile(plugin, owner.getEyeLocation().subtract(0, -1, 0), name, firing);
            projectile.setFirer(owner);
            projectile.launchProjectile();
        }
    }

    class InkProjectile extends EntityProjectile {

        public InkProjectile(Plugin plugin, Location fireLocation, String name, Entity projectile) {
            super(plugin, fireLocation, name, projectile);
            this.setDamage(1.5);
            this.setSpeed(1 + Math.random() * 0.5);
            this.setKnockback(1.0);
            this.setUpwardKnockback(0.5);
            this.setHitboxSize(0.5);
            this.setSpread(15);
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




