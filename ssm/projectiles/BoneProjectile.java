package ssm.projectiles;

import ssm.events.SmashDamageEvent;
import ssm.utilities.VelocityUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BoneProjectile extends SmashProjectile {

    public BoneProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 0.7;
        this.hitbox_size = 0.25;
        this.knockback_mult = 0;
        this.expiration_ticks = 40;
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack bone = new ItemStack(Material.BONE);
        return firer.getWorld().dropItem(firer.getLocation().add(Math.random() * 5 - 2.5, Math.random() * 3, Math.random() * 5 - 2.5), bone);
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                0.6 + 0.3 * Math.random(), false, 0, 0, 0.3, false);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        if(!(hit instanceof Player)) {
            return true;
        }
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        if(smashDamageEvent.isCancelled()) {
            return true;
        }
        VelocityUtil.setVelocity(hit, projectile.getVelocity());
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        return true;
    }

    @Override
    protected boolean onIdle() {
        return true;
    }

}
