package SSM.Projectiles;

import SSM.Events.SmashDamageEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockProjectile extends SmashProjectile {

    protected double max_damage;
    protected long charge;
    protected double mult;
    protected int block_id;
    protected byte block_data;
    protected FallingBlock hit_block_effect;

    public BlockProjectile(Player firer, String name, long charge, double mult, int block_id, byte block_data) {
        super(firer, name);
        this.damage = 8;
        this.max_damage = 9;
        this.hitbox_mult = 1;
        this.knockback_mult = 2.5;
        this.charge = charge;
        this.mult = mult;
        this.block_id = block_id;
        this.block_data = block_data;
    }

    @Override
    protected Entity createProjectileEntity() {
        FallingBlock block = firer.getWorld().spawnFallingBlock(firer.getEyeLocation().add(firer.getLocation().getDirection()),
                block_id, block_data);
        return block;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                mult, false, 0.2, 0, 1, true);
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
        double block_damage = Math.min(max_damage, projectile.getVelocity().length() * damage);
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, block_damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        if (projectile instanceof FallingBlock) {
            FallingBlock thrown = (FallingBlock) projectile;
            hit_block_effect = projectile.getWorld().spawnFallingBlock(projectile.getLocation(), thrown.getMaterial(), (byte) thrown.getBlockData());
        }
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        return false;
    }

    @Override
    protected boolean onIdle() {
        return false;
    }

    // Prevents falling blocks from forming
    // This is mainly for the falling block spawned
    // As an effect after a livingentity is hit
    @EventHandler
    public void blockForm(EntityChangeBlockEvent e) {
        if (!(e.getEntity() instanceof FallingBlock)) {
            return;
        }
        FallingBlock falling = (FallingBlock) e.getEntity();
        if(e.getEntity().equals(projectile)) {
            falling.getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, falling.getBlockId());
            cancel();
        }
        if(e.getEntity().equals(hit_block_effect)) {
            falling.getWorld().playEffect(e.getBlock().getLocation(), Effect.STEP_SOUND, falling.getBlockId());
            falling.remove();
        }
        e.setCancelled(true);
    }

}
