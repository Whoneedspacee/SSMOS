package xyz.whoneedspacee.ssmos.projectiles.original;

import xyz.whoneedspacee.ssmos.abilities.original.WoolMine;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.BlockRestoreManager;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.projectiles.SmashProjectile;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WoolProjectile extends SmashProjectile {

    public WoolProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 4;
        this.hitbox_size = 0.5;
        this.knockback_mult = 2;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        firer.getWorld().playSound(firer.getLocation(), Sound.SHEEP_IDLE, 2f, 1.5f);
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack wool = new ItemStack(Material.WOOL);
        return firer.getWorld().dropItem(firer.getEyeLocation(), wool);
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(), 1, false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        solidify(false);
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        solidify(false);
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        solidify(false);
        return true;
    }

    @Override
    protected boolean onIdle() {
        solidify(false);
        return true;
    }

    public void solidify(boolean inform) {
        Block block = projectile.getLocation().getBlock();
        BlockRestoreManager.ourInstance.restore(block);
        Kit kit = KitManager.getPlayerKit(firer);
        if (kit != null) {
            WoolMine mine = kit.getAttributeByClass(WoolMine.class);
            if (mine != null) {
                mine.wool_block = block;
                mine.block_material = block.getType();
                mine.block_data = block.getData();
                mine.arm_time = System.currentTimeMillis();
                mine.last_delay_time = System.currentTimeMillis();
                mine.projectile = null;
            }
        }
        block.setType(Material.WOOL);
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
        if (inform) {
            firer.getWorld().playSound(firer.getLocation(), Sound.SHEEP_IDLE, 2f, 1.5f);
            Utils.sendAttributeMessage("You armed", name, firer, ServerMessageType.GAME);
        }
        destroy();
    }

}
