package xyz.whoneedspacee.ssmos.attributes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;

public class ArrowDamageMultiplier extends Attribute {

    protected double damage_multiplier;

    public ArrowDamageMultiplier(double damage_multiplier) {
        this.name = "Arrow Damage Reduction";
        this.description = new String[] {
                ChatColor.RESET + "Arrows deal " + String.format("%.1f", 1 - damage_multiplier) + " less damage.",
        };
        this.damage_multiplier = damage_multiplier;
    }

    @Override
    public void activate() {
        return;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ownerDamageWithArrow(SmashDamageEvent e) {
        if(owner == null || !owner.equals(e.getDamager())) {
            return;
        }
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        e.setDamage(e.getDamage() * damage_multiplier);
    }

}
