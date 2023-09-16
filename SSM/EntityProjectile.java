package SSM;

import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class EntityProjectile extends BukkitRunnable {

    protected Plugin plugin;
    private Location fireLocation;
    protected String name;
    protected boolean expAdd = false;
    protected Entity projectile;
    protected Player firer;
    private double time;
    private boolean timed = false;
    private boolean lastsOnGround = false;
    private boolean fireOpposite = false;
    private boolean clearOnFinish = true;
    private boolean direct = false;
    private boolean fired;
    private boolean pierce;
    private double damage;
    private double speed;
    private double knockback;
    private double upwardKnockback;
    private double hitboxRange;
    private double spread;
    private int hungerGain;

    public EntityProjectile(Plugin plugin, Location fireLocation, String name, Entity projectile) {
        this.plugin = plugin;
        this.fireLocation = fireLocation;
        this.name = name;
        this.projectile = projectile;
    }

    public void launchProjectile() {
        if (timed) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    projectile.remove();
                }
            }, (long) (time * 20));
        }
        if (fired) {
            return;
        }
        fired = true;
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                projectile.teleport(fireLocation);
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
        double yaw = Math.atan2(direction.getZ(), direction.getX()) + EntityProjectile.getRandomSpread(spread);
        double xz = Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ());
        double pitch = Math.atan2(direction.getY(), xz)  + EntityProjectile.getRandomSpread(spread);
        direction.setX(Math.cos(pitch) * Math.cos(yaw));
        direction.setZ(Math.cos(pitch) * Math.sin(yaw));
        direction.setY(Math.sin(pitch));
        if (direct) {
            projectile.setVelocity(direction.multiply(magnitude).setY(0).normalize());
        } else {
            projectile.setVelocity(direction.multiply(magnitude));
        }
        this.runTaskTimer(plugin, 0L, 1L);
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
            if (entity.getName().equalsIgnoreCase(projectile.getName())) {
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
            if (target.getNoDamageTicks() > 1) {
                target.setNoDamageTicks(0);
            }
            double damage = getDamage();
            DamageUtil.dealDamage(firer, target, damage, true, expAdd);
            firer.setFoodLevel(firer.getFoodLevel() + hungerGain);
            double knockback = getKnockback();
            double upwardKnockback = getUpwardKnockback();
            Vector velocity = projectile.getVelocity();
            if (upwardKnockback != 0) {
                VelocityUtil.addKnockback(firer, target, knockback, 0.5);
            } else {
                velocity = velocity.normalize().multiply(knockback);
                target.setVelocity(velocity);
            }
        } else {
            if (!lastsOnGround) {
                projectile.remove();
            }
            onBlockHit();
        }
        if (!pierce) {
            clearProjectile();
        }
        return success;
    }

    public void onBlockHit() {
        clearProjectile();
    }

    public boolean clearProjectile() {
        if (getClearOnFinish()) {
            projectile.remove();
            return true;
        }
        return false;
    }

    public static double getRandomSpread(double range) {
        double randomAngle = Math.random() * range / 2;
        if (new Random().nextBoolean()) {
            randomAngle *= -1;
        }
        return randomAngle * Math.PI / 180;
    }

    public Player getFirer() {
        return firer;
    }

    public void setFirer(Player firer) { this.firer = firer; }

    public boolean getFireOpposite() {
        return fireOpposite;
    }

    public void setFireOpposite(boolean fireOpposite) {
        this.fireOpposite = fireOpposite;
    }

    public boolean getClearOnFinish() {
        return clearOnFinish;
    }

    public void setClearOnFinish(boolean clearOnFinish) {
        this.clearOnFinish = clearOnFinish;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getKnockback() {
        return knockback;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }

    public double getUpwardKnockback() {
        return upwardKnockback;
    }

    public void setUpwardKnockback(double upwardKnockback) {
        this.upwardKnockback = upwardKnockback;
    }

    public double getHitboxSize() {
        return hitboxRange;
    }

    public void setHitboxSize(double hitboxRange) {
        this.hitboxRange = hitboxRange;
    }

    public double getSpread() {
        return spread;
    }

    public void setSpread(double spread) {
        this.spread = spread;
    }

    public boolean getExpAdd() {
        return expAdd;
    }

    public void setExpAdd(boolean expAdd) {
        this.expAdd = expAdd;
    }

    public boolean getPierce() {
        return pierce;
    }

    public void setPierce(boolean pierce) {
        this.pierce = pierce;
    }

    public boolean getLastsOnGround() {
        return lastsOnGround;
    }

    public void setLastsOnGround(boolean lastsOnGround) {
        this.lastsOnGround = lastsOnGround;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean getTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public boolean getDirect() {
        return direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }

}
