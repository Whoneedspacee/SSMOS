package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SulphurBomb extends Ability implements OwnerRightClickEvent {

    public SulphurBomb() {
        super();
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_DEATH, 2f, 1.5f);
        ItemStack sulphur = new ItemStack(Material.COAL, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), sulphur);
        BombProjectile projectile = new BombProjectile(plugin, name, firing);
        projectile.setFirer(owner);
        projectile.launchProjectile();
    }

    class BombProjectile extends EntityProjectile {

        public BombProjectile(Plugin plugin, String name, Entity projectile) {
            super(plugin, name, projectile);
            this.setDamage(6.0);
            this.setHitboxSize(0.5);
            this.setKnockback(2.5);
        }

        @Override
        public void doVelocity() {
            VelocityUtil.setVelocity(projectile, owner.getLocation().getDirection(),
                    1, false, 0, 0.2, 10, false);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            owner.getWorld().playEffect(projectile.getLocation(), Effect.EXPLOSION_LARGE, 1);
            return super.onHit(target);
        }
    }
}