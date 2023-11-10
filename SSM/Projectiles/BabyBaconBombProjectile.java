package SSM.Projectiles;

import SSM.Events.SmashDamageEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BabyBaconBombProjectile extends SmashProjectile {

    private int pig_task;

    public BabyBaconBombProjectile(Player firer, String name) {
        super(firer, name);
        this.expiration_ticks = 80;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
    }

    @Override
    protected Entity createProjectileEntity() {
        Pig pig = firer.getWorld().spawn(firer.getLocation(), Pig.class);
        pig.setHealth(5);
        pig.setBaby();
        pig_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(SSM.SSM.getInstance(), new Runnable() {
            @Override
            public void run() {
                EntityCreature ec = ((CraftCreature)projectile).getHandle();
                HashMap<LivingEntity, Double> targets = Utils.getInRadius(projectile.getLocation(), 20);
                for (LivingEntity target : targets.keySet()){
                    if (target.equals(firer)){
                        continue;
                    }
                    Location targetLocation = target.getLocation();
                    ec.getControllerMove().a(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ(), 1.2);
                }
            }
        }, 0L, 1L);
        return pig;
    }

    @Override
    protected void doVelocity() {
        projectile.setVelocity(new Vector(0, -0.4, 0));
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        explodeEffect();
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        this.cancel();
        return false;
    }

    @Override
    protected boolean onIdle() {
        this.cancel();
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        Bukkit.getScheduler().cancelTask(pig_task);

    }

    protected void explodeEffect() {
        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, projectile.getLocation().add(0, 0.5, 0), 0, 0, 0, 0, 1, 96, projectile.getWorld().getPlayers());
        projectile.getWorld().playSound(projectile.getLocation(), Sound.EXPLODE, 0.6f, 2f);
        projectile.getWorld().playSound(projectile.getLocation(), Sound.PIG_DEATH, 1f, 2f);

        HashMap<LivingEntity, Double> targets = Utils.getInRadius(projectile.getLocation(), 4);
        for (LivingEntity target : targets.keySet()){
            if (target.equals(firer)){
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(target, firer, 4);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
        }
    }

}
