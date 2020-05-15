package SSM;

import SSM.Kits.*;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EntityProjectile extends BukkitRunnable {

    protected Plugin plugin;
    protected Player firer;
    protected String name;
    protected boolean expAdd = false;
    protected Entity projectile;
    private Location overridePosition;
    private double time;
    private boolean timed = false;
    private boolean lastsOnGround = false;
    private boolean fireOpposite = false;
    private boolean clearOnFinish = true;
    private boolean direct = false;
    private boolean fired;
    private boolean upwardKnockbackSet = true;
    private boolean pierce;
    private double[] data;

    public EntityProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
        this.plugin = plugin;
        this.firer = firer;
        this.name = name;
        this.projectile = projectile;
        this.data = new double[]{0, 0, 0, 0, 0, 0};
    }

    public double getRandomVariation() {
        double variation = getVariation();
        double randomAngle = Math.random() * variation / 2;
        if (new Random().nextBoolean()) {
            randomAngle *= -1;
        }
        return randomAngle * Math.PI / 180;
    }

    public void launchProjectile() {
        if (timed){
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    projectile.remove();
                }
            }, (long)(time*20));
        }
        firer.setLevel(0);
        if (fired) {
            return;
        }
        if (getOverridePosition() == null) {
            setOverridePosition(firer.getEyeLocation());
        }
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                projectile.teleport(getOverridePosition());
                /*
                The reason we need this is because if we don't have it, the teleport
                literally doesn't work because apparently it's "too fast" and
                tries to teleport the entity before it exists
                (learned the hard way)
                 */
            }
        }, 2L);
        projectile.setCustomName(name);
        if (projectile instanceof Item) {
            Item item = (Item) projectile;
            item.setPickupDelay(1000000);
        }
        double magnitude = getSpeed();
        Vector direction = firer.getLocation().getDirection();
        if (getFireOpposite()) {
            direction.multiply(-1);
        }
        direction.rotateAroundX(getRandomVariation());
        direction.rotateAroundY(getRandomVariation());
        direction.rotateAroundZ(getRandomVariation());
        if (direct){
            projectile.setVelocity(direction.multiply(magnitude).setY(0).normalize());
        }else{
            projectile.setVelocity(direction.multiply(magnitude));
        }
        this.runTaskTimer(plugin, 0L, 4L);
        fired = true;
    }

    @Override
    public void run() {
        if (projectile.isDead() || !projectile.isDead() && projectile.isOnGround()) {
            onHit(null);
            this.cancel();
            return;
        }
        double hitboxRange = getHitboxSize();
        List<Entity> canHit = projectile.getNearbyEntities(hitboxRange, hitboxRange, hitboxRange);
        canHit.remove(projectile);
        canHit.remove(firer);
        if (canHit.size() <= 0) {
            return;
        }
        for (Entity entity : canHit) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (entity.getName().equalsIgnoreCase(projectile.getName())){
                continue;
            }
            LivingEntity target = (LivingEntity) canHit.get(0);
            onHit(target);
            break;
        }
    }

    public boolean onHit(LivingEntity target) {
        boolean success = target != null;
        if (success) {
            if (target.getNoDamageTicks() > 1){
                target.setNoDamageTicks(0);
            }
            double damage = getDamage();
            DamageUtil.dealDamage(firer, target, damage, true, expAdd);
            double knockback = getKnockback();
            double upwardKnockback = getUpwardKnockback();
            Vector velocity = projectile.getVelocity();
            if (upwardKnockbackSet) {
                VelocityUtil.addKnockback(firer, target, knockback, 0.5);
            } else {
                velocity = velocity.normalize().multiply(knockback);
                target.setVelocity(velocity);
            }
        }else{
            if (!lastsOnGround){
                projectile.remove();
            }
            onBlockHit();
        }
        if (!pierce){
            clearProjectile();
        }
        return success;
    }

    public void onBlockHit(){

    }

    public boolean clearProjectile() {
        if (getClearOnFinish()) {
            projectile.remove();
            return true;
        }
        return false;
    }

    public void setOverridePosition(Location overridePosition) {
        this.overridePosition = overridePosition;
    }

    public Location getOverridePosition() {
        return overridePosition;
    }


    public void setFireOpposite(boolean fireOpposite) {
        this.fireOpposite = fireOpposite;
    }

    public boolean getFireOpposite() {
        return fireOpposite;
    }

    public void setClearOnFinish(boolean clearOnFinish) {
        this.clearOnFinish = clearOnFinish;
    }

    public boolean getClearOnFinish() {
        return clearOnFinish;
    }

    public void setDamage(double damage) {
        data[0] = damage;
    }

    public double getDamage() {
        return data[0];
    }

    public void setSpeed(double speed) {
        data[1] = speed;
    }

    public double getSpeed() {
        return data[1];
    }

    public void setKnockback(double knockback) {
        data[2] = knockback;
    }

    public double getKnockback() {
        return data[2];
    }

    public void setUpwardKnockback(double upwardKnockback) {
        data[3] = upwardKnockback;
        upwardKnockbackSet = true;
    }

    public double getUpwardKnockback() {
        return data[3];
    }

    public void setHitboxSize(double hitboxRange) {
        data[4] = hitboxRange;
    }

    public double getHitboxSize() {
        return data[4];
    }

    public void setVariation(double variation) {
        data[5] = variation;
    }

    public double getVariation() {
        return data[5];
    }

    public boolean getExpAdd(){return expAdd;}

    public void setExpAdd(boolean expAdd1){expAdd = expAdd1;}

    public boolean getPierce(){return pierce;}

    public void setPierce(boolean pierceBoolean){pierce = pierceBoolean;}

    public boolean getLastsOnGround(){return lastsOnGround;}

    public void setLastsOnGround(boolean lastsOnGround1){lastsOnGround = lastsOnGround1;}

    public double getTime(){return time;}

    public void setTime(double time1){time = time1;}

    public boolean getTimed(){return timed;}

    public void setTimed(boolean timed1){timed = timed1;}

    public void setDirect(boolean direct1){direct = direct1;}

}












