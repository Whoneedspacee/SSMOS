package ssm.projectiles;

import ssm.events.SmashDamageEvent;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ScatterProjectile extends SmashProjectile {

    public double projectile_velocity = 0;
    public double spread = 0;
    public Vector projectile_direction;
    public long tick_effect_rate = 1;
    public EnumParticle particle_type;

    public ScatterProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 4;
        this.hitbox_size = 0.5;
        this.knockback_mult = 1;
        this.expiration_ticks = 100;
    }

    @Override
    protected Entity createProjectileEntity() {
        return null;
    }

    @Override
    protected void doVelocity() {
        Vector direction = projectile_direction.clone().add(new Vector(
                (Math.random() - 0.5) * spread, (Math.random() - 0.5) * spread, (Math.random() - 0.5) * spread));
        projectile.setVelocity(direction);
    }

    @Override
    protected void doEffect() {
        if(projectile.getTicksLived() % tick_effect_rate != 0) {
            return;
        }
        Utils.playParticle(particle_type, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                projectile.getWorld().getPlayers());
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
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
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
