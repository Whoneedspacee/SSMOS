package SSM.Projectiles;

import SSM.Events.SmashDamageEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
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
        this.knockback_mult = 1; //temp
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
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        bounceBack(true);
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        bounceBack(false);
        this.cancel();
        return false;
    }

    @Override
    protected boolean onIdle() {
        this.cancel();
        return false;
    }

    protected void bounceBack(boolean hitEntity){
        if (projectile instanceof Item){
            Item pork = (Item) projectile;
            pork.setPickupDelay(5);
            if (hitEntity) pork.setItemStack(new ItemStack(Material.GRILLED_PORK));
        }
        firer.getWorld().playSound(firer.getLocation(), Sound.ITEM_PICKUP, 1f, 0.5f);
        Vector playerVector = firer.getLocation().toVector();
        Vector projectileVector = projectile.getLocation().toVector();

        double mult = 0.5 + (0.035 * (playerVector.clone().subtract(projectileVector).length()));
        projectile.setVelocity(playerVector.subtract(projectileVector).normalize().add(new Vector(0, 0.4, 0)).multiply(mult));
    }

}
