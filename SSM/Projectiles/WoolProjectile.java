package SSM.Projectiles;

import SSM.Abilities.WoolMine;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.BlockRestoreManager;
import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
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
            WoolMine mine = (WoolMine) kit.getAttributeByClass(WoolMine.class);
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
