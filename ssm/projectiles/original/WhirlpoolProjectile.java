package ssm.projectiles.original;

import ssm.events.SmashDamageEvent;
import ssm.projectiles.SmashProjectile;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WhirlpoolProjectile extends SmashProjectile {

    public WhirlpoolProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 4;
        this.hitbox_size = 0.5;
        this.knockback_mult = 0;
        this.expiration_ticks = 60;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        firer.playSound(firer.getLocation(), Sound.DIG_SNOW, 1f, 1f);
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack shard = new ItemStack(Material.PRISMARINE_SHARD);
        return firer.getWorld().dropItem(firer.getEyeLocation(), shard);
    }

    @Override
    protected void doVelocity() {
        projectile.setVelocity(firer.getLocation().getDirection().multiply(1.6));
    }

    @Override
    protected void doEffect() {
        Utils.playParticle(EnumParticle.DRIP_WATER, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.01f, 1, 96, projectile.getWorld().getPlayers());
    }

    @Override
    protected boolean onExpire() {
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setIgnoreArmor(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        if(smashDamageEvent.isCancelled()) {
            return true;
        }
        Vector trajectory = firer.getLocation().toVector().subtract(hit.getLocation().toVector()).normalize();
        trajectory.setY(0.5);
        VelocityUtil.setVelocity(hit, trajectory);
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
