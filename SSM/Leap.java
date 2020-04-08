package SSM;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import java.util.List;

public abstract class Leap extends Ability {
    private double power;
    private boolean active;
    private double activeTime;
    private double damage;
    private double knockback;
    private boolean recoil;
    private int bukkit = -1;
    private int i = 0;


    public Leap() {
        super();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getPower() {
        return this.power;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActiveTime(double activeTime) {
        this.activeTime = activeTime;
    }

    public double getActiveTime() {
        return this.activeTime;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }

    public double getKnockback() {
        return this.knockback;
    }

    public void setRecoil(boolean recoil) {
        this.recoil = recoil;
    }

    public boolean getRecoil() {
        return this.recoil;
    }


    public void activate() {
        i = 0;
        owner.setVelocity(owner.getLocation().getDirection().multiply(getPower()));
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                active = false;
            }
        }, (long) getActiveTime() * 20);
        bukkit = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                i++;
                if (active) {
                    List nearby = owner.getNearbyEntities(0.5, 0.5, 0.5);
                    nearby.remove(owner);
                    if (nearby.isEmpty()) {
                        return;
                    }
                    if (!(nearby.get(0) instanceof LivingEntity)) {
                        return;
                    }
                    LivingEntity target = (LivingEntity)nearby.get(0);
                    target.damage(damage);
                    stop();
                }
                if (i >= (activeTime * 20)) {
                    stop();
                }
            }
        }, 0, 1);


    }

    private void stop() {
        Bukkit.getScheduler().cancelTask(bukkit);
        active = false;
    }


}



