package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
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

public class SulphurBomb extends Ability implements OwnerRightClickEvent {

    public SulphurBomb() {
        super();
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_CREEPER_DEATH, 10L, 1L);
        ItemStack sulphur = new ItemStack(Material.COAL, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), sulphur);
        BombProjectile projectile = new BombProjectile(plugin, owner, name, firing);
        projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
        projectile.launchProjectile();
    }

    class BombProjectile extends EntityProjectile {

        public BombProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(6.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setVariation(0);
            this.setKnockback(2.5);
            this.setUpwardKnockback(0.5);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            owner.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, projectile.getLocation(), 1);
            return super.onHit(target);
        }
    }
}