package SSM.Projectiles;

import SSM.Events.SmashDamageEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class InkProjectile extends SmashProjectile {

    protected double spread;

    public InkProjectile(Player firer, String name, double spread) {
        super(firer, name);
        this.damage = 1.725;
        this.hitbox_size = 0.5;
        this.knockback_mult = 3;
        this.spread = spread;
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack ink = new ItemStack(Material.INK_SACK);
        return firer.getWorld().dropItem(firer.getEyeLocation().add(firer.getLocation().getDirection()), ink);
    }

    @Override
    protected void doVelocity() {
        Vector random = new Vector((Math.random() - 0.5), (Math.random() - 0.5), (Math.random() - 0.5));
        random.multiply(spread);
        random.normalize();
        random.multiply(0.15);
        if(spread == 0) {
            random.zero();
        }
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection().add(random),
                1 + 0.4 * Math.random(), false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        // Do the effect every 3 ticks
        if(projectile.getTicksLived() % 3 != 0) {
            return;
        }
        Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                projectile.getWorld().getPlayers());
    }

    @Override
    protected boolean onExpire() {
        explodeEffect();
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        explodeEffect();
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        explodeEffect();
        return true;
    }

    @Override
    protected boolean onIdle() {
        explodeEffect();
        return true;
    }

    protected void explodeEffect() {
        projectile.getWorld().playSound(projectile.getLocation(), Sound.EXPLODE, 0.75f, 1.25f);
    }

}
