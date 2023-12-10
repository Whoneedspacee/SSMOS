package ssm.projectiles.ssmos;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ssm.events.SmashDamageEvent;
import ssm.projectiles.SmashProjectile;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

public class FlameFissureProjectile extends SmashProjectile {

    protected Block block;
    protected double angle;
    protected Entity burned_entity = null;
    protected int burn_ticks = 20;

    public FlameFissureProjectile(Player firer, String name, Block block, double angle) {
        super(firer, name);
        this.damage = 1.5;
        this.hitbox_size = 0.5;
        this.knockback_mult = 0;
        this.block = block;
        this.angle = angle;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.0f, 1, 96,
                projectile.getWorld().getPlayers());
    }

    @Override
    protected Entity createProjectileEntity() {
        Material material = Math.random() > 0.5 ? Material.BLAZE_POWDER : Material.COAL;
        ItemStack fire = new ItemStack(material);
        return block.getWorld().dropItem(block.getLocation().add(0.5, 1.5, 0.5), fire);
    }

    @Override
    protected void doVelocity() {
        Vector random = new Vector(0, 2 + Math.random() * 2, 0);
        random.setX(Math.cos(Math.toRadians(angle)));
        random.setZ(Math.sin(Math.toRadians(angle)));
        VelocityUtil.setVelocity(projectile, random, 0.25 + 1 * Math.random(),
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
        int fire_ticks = Math.min(160, Math.max(0, hit.getFireTicks()) + burn_ticks);
        hit.setFireTicks(fire_ticks);
        burned_entity = hit;
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(burned_entity.getFireTicks() <= 0) {
                    burned_entity = null;
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(plugin, 0L ,5L);
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

    @EventHandler
    public void armorPiercingFire(SmashDamageEvent e) {
        if(e.getDamager() != null && e.getDamager().equals(firer)) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.FIRE_TICK) {
            return;
        }
        if(burned_entity == null || !burned_entity.equals(e.getDamagee())) {
            return;
        }
        e.setDamager(firer);
        e.setDamagerName(firer.getName());
        e.setIgnoreArmor(true);
        e.multiplyKnockback(0);
    }

}
