package ssm.projectiles.ssmos;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import ssm.events.SmashDamageEvent;
import ssm.managers.DamageManager;
import ssm.projectiles.SmashProjectile;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MagmaBoulderProjectile extends SmashProjectile {

    protected int block_id;
    protected byte block_data;
    protected int max_bounces = 3;
    protected int hit_times = 0;
    protected HashMap<LivingEntity, Long> last_hit_time_ms = new HashMap<LivingEntity, Long>();

    public MagmaBoulderProjectile(Player firer, String name, int block_id, byte block_data) {
        super(firer, name);
        this.damage = 4;
        this.hitbox_size = 0.75;
        this.knockback_mult = 1;
        this.block_id = block_id;
        this.block_data = block_data;
        this.blockDetection = false;
    }

    @Override
    public void run() {
        super.run();
        if(projectile != null) {
            PacketPlayOutEntityTeleport teleport_packet = new PacketPlayOutEntityTeleport(((CraftEntity) projectile).getHandle());
            for(Player player : projectile.getWorld().getPlayers()) {
                Utils.sendPacket(player, teleport_packet);
            }
            projectile.setVelocity(projectile.getVelocity().setY(Math.max(projectile.getVelocity().getY() - 0.02, -4.0)));
        }
        if(projectile != null && projectile.getVelocity().getY() < 0) {
            if(Utils.entityIsOnGround(projectile, Math.abs(projectile.getVelocity().getY()) + 0.75) && hit_times < max_bounces) {
                doExplosion();
                Vector velocity = projectile.getVelocity();
                velocity.setY(Math.abs(velocity.getY() * 0.8));
                projectile.setVelocity(velocity);
                hit_times++;
            }
        }
        // Check if the block formed
        if(projectile != null && !projectile.isValid()) {
            doExplosion();
            projectile = null;
        }
    }

    @Override
    protected Entity createProjectileEntity() {
        Location spawn_location = firer.getEyeLocation().add(firer.getLocation().getDirection());
        WorldServer world = ((CraftWorld) firer.getWorld()).getHandle();
        EntityFallingBlock entity = new EntityFallingBlock(world, spawn_location.getX(), spawn_location.getY(), spawn_location.getZ(),
                net.minecraft.server.v1_8_R3.Block.getById(block_id).fromLegacyData(block_data));
        entity.ticksLived = 1;
        world.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        FallingBlock block = (FallingBlock) entity.getBukkitEntity();
        DamageManager.no_fast_block.put(block, 1);
        return block;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1.2, false, 0.2, 0, 1, true);
    }

    @Override
    protected void doEffect() {
        firer.getWorld().playSound(projectile.getLocation(), Sound.FIRE, 1.4f, 0.8f);
        Utils.playParticle(EnumParticle.FLAME, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.25f, 1, 96,
                projectile.getWorld().getPlayers());
    }

    @Override
    protected boolean onExpire() {
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        last_hit_time_ms.putIfAbsent(hit, 0L);
        if(System.currentTimeMillis() - last_hit_time_ms.get(hit) < 500) {
            return false;
        }
        last_hit_time_ms.put(hit, System.currentTimeMillis());
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage * (hit_times + 1));
        smashDamageEvent.multiplyKnockback(knockback_mult + hit_times * 0.5);
        if(!Utils.entityIsOnBlock(hit)) {
            smashDamageEvent.multiplyKnockback(1.5);
        }
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        return false;
    }

    @Override
    protected boolean onIdle() {
        return false;
    }

    public void doExplosion() {
        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                projectile.getWorld().getPlayers());
        projectile.getWorld().playSound(projectile.getLocation(), Sound.EXPLODE, 1.0f, 0.5f);
    }

}
