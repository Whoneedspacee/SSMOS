package SSM.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class SmashDamageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;
    private LivingEntity damagee;
    private LivingEntity damager;
    private final long damage_time_ms;
    private double damage;
    private double knockbackMultiplier;
    private boolean ignoreArmor;
    private boolean ignoreDamageDelay;
    private DamageCause damageCause;
    private Location origin;
    private String reason;
    private Projectile projectile;
    private String damagee_name;
    private String damager_name;


    public SmashDamageEvent(LivingEntity damagee, LivingEntity damager, double damage) {
        if(damagee == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Warning: Damagee was null in SmashDamageEvent!");
        }
        this.damagee = damagee;
        this.damager = damager;
        this.damage_time_ms = System.currentTimeMillis();
        this.damage = damage;
        this.knockbackMultiplier = 1;
        this.ignoreArmor = false;
        this.ignoreDamageDelay = false;
        this.damageCause = DamageCause.CUSTOM;
        this.origin = null;
        this.reason = "N/A";
        this.projectile = null;
        if(damagee != null) {
            this.damagee_name = damagee.getName();
        }
        if(damager != null) {
            this.damager_name = damager.getName();
        }
    }

    public void setDamagee(LivingEntity damagee) {
        this.damagee = damagee;
    }

    public LivingEntity getDamagee() {
        return damagee;
    }

    public void setDamager(LivingEntity damager) {
        this.damager = damager;
    }

    public LivingEntity getDamager() {
        return damager;
    }

    public long getDamageTimeMs() {
        return damage_time_ms;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return damage;
    }

    public void multiplyKnockback(double knockbackMultiplier) {
        this.knockbackMultiplier *= knockbackMultiplier;
    }

    public double getKnockbackMultiplier() {
        return knockbackMultiplier;
    }

    public void setIgnoreArmor(boolean ignoreArmor) {
        this.ignoreArmor = ignoreArmor;
    }

    public boolean getIgnoreArmor() {
        return ignoreArmor;
    }

    public void setIgnoreDamageDelay(boolean ignoreDamageDelay) {
        this.ignoreDamageDelay = ignoreDamageDelay;
    }

    public boolean getIgnoreDamageDelay() {
        return ignoreDamageDelay;
    }

    public void setDamageCause(DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public void setKnockbackOrigin(Location origin) {
        this.origin = origin;
    }

    public Location getKnockbackOrigin() {
        return origin;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setProjectile(Projectile projectile) {
        this.projectile = projectile;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    public void setDamageeName(String damagee_name) {
        this.damagee_name = damagee_name;
    }

    public String getDamageeName() {
        return damagee_name;
    }

    public void setDamagerName(String damager_name) {
        this.damager_name = damager_name;
    }

    public String getDamagerName() {
        return damager_name;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof SmashDamageEvent)) {
            return false;
        }
        SmashDamageEvent check = (SmashDamageEvent) o;
        boolean to_return = true;
        if(check.getDamagerName() == null) {
            to_return = to_return && getDamagerName() == null;
        }
        else {
            to_return = to_return && check.getDamagerName().equals(getDamagerName());
        }
        if(check.getDamageeName() == null) {
            to_return = to_return && getDamageeName() == null;
        }
        else {
            to_return = to_return && check.getDamageeName().equals(getDamageeName());
        }
        if(check.getReason() == null) {
            to_return = to_return && getReason() == null;
        }
        else {
            to_return = to_return && check.getReason().equals(getReason());
        }
        return to_return;
    }

}
