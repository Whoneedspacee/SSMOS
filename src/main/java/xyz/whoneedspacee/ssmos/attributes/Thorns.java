package xyz.whoneedspacee.ssmos.attributes;

import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;

public class Thorns extends Attribute {

    protected double health_threshold;
    protected double damage_multiplier;
    protected double knockback_multiplier;

    public Thorns(double health_threshold, double damage_multiplier, double knockback_multiplier) {
        super();
        this.name = "Thorns";
        this.usage = AbilityUsage.PASSIVE;
        this.health_threshold = health_threshold;
        this.damage_multiplier = damage_multiplier;
        this.knockback_multiplier = knockback_multiplier;
        this.description = new String[] {
                ChatColor.RESET + "Takes 66% less damage and knockback from whoneedspacee.ssmos.projectiles",
                ChatColor.RESET + "when under 10 health.",
        };
    }

    public void activate() {
        return;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkOwnerDamage(SmashDamageEvent e) {
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(!(e.getDamager() instanceof Player)) {
            return;
        }
        if(!(e.getDamagee() instanceof Player)) {
            return;
        }
        if(!e.getDamagee().equals(owner)) {
            return;
        }
        if(owner.getHealth() >= health_threshold) {
            return;
        }
        e.setDamage(e.getDamage() * damage_multiplier);
        e.multiplyKnockback(knockback_multiplier);
    }

}
