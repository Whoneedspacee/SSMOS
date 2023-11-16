package ssm.projectiles;

import ssm.events.SmashDamageEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.VelocityUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BaconProjectile extends SmashProjectile {

    public BaconProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 4;
        this.hitbox_size = 0.58;
        this.knockback_mult = 1;
        this.expiration_ticks = 100;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        firer.getWorld().playSound(firer.getLocation(), Sound.PIG_IDLE, 2f, 1.5f);
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack bacon = new ItemStack(Material.PORK);
        return firer.getWorld().dropItem(firer.getEyeLocation(), bacon);
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1.2, false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        bounceBack();
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        if (projectile instanceof Item && DamageUtil.canDamage(hit, firer)) {
            Item pork = (Item) projectile;
            pork.setItemStack(new ItemStack(Material.GRILLED_PORK));
        }
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        playHitSound();
        bounceBack();
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        bounceBack();
        this.cancel();
        return false;
    }

    @Override
    protected boolean onIdle() {
        bounceBack();
        this.cancel();
        return false;
    }

    protected void bounceBack(){
        if (projectile instanceof Item){
            Item pork = (Item) projectile;
            pork.setPickupDelay(5);
        }
        firer.getWorld().playSound(firer.getLocation(), Sound.ITEM_PICKUP, 1f, 0.5f);
        double mult = 0.5 + (0.035 * (firer.getLocation().distance(projectile.getLocation())));
        Vector playerVector = firer.getLocation().toVector();
        Vector projectileVector = projectile.getLocation().toVector();
        projectile.setVelocity(playerVector.subtract(projectileVector).normalize().add(new Vector(0, 0.4, 0)).multiply(mult));
    }

}
