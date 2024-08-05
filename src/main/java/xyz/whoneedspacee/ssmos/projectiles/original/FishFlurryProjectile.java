package xyz.whoneedspacee.ssmos.projectiles.original;

import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.projectiles.SmashProjectile;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FishFlurryProjectile extends SmashProjectile {

    protected Block block;

    public FishFlurryProjectile(Player firer, String name, Block block) {
        super(firer, name);
        this.damage = 2;
        this.hitbox_size = 0.5;
        this.knockback_mult = 0;
        this.block = block;
    }

    @Override
    protected Entity createProjectileEntity() {
        ItemStack fish = new ItemStack(Material.RAW_FISH);
        fish.setDurability((byte) (Math.random() * 4));
        return block.getWorld().dropItem(block.getLocation().add(0.5, 1.5, 0.5), fish);
    }

    @Override
    protected void doVelocity() {
        Vector random = new Vector(Math.random() - 0.5, 1 + Math.random() * 1, Math.random() - 0.5);
        VelocityUtil.setVelocity(projectile, random, 0.25 + 0.4 * Math.random(),
                false, 0, 0.2, 10, false);
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
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setKnockbackOrigin(hit.getLocation().add(Math.random() - 0.5, -0.1, Math.random() - 0.5));
        smashDamageEvent.setReason(name);
        Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, hit.getLocation().add(0, 1, 0),
                1f, 1f, 1f, 0, 12, 96, hit.getWorld().getPlayers());
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
