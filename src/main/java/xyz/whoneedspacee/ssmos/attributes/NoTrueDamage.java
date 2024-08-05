package xyz.whoneedspacee.ssmos.attributes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;

public class NoTrueDamage extends Attribute {

    protected double damage_multiplier;

    public NoTrueDamage(double damage_multiplier) {
        super();
        this.name = "No True Damage";
        this.damage_multiplier = damage_multiplier;
    }

    public void activate() {
        return;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSmashDamage(SmashDamageEvent e) {
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        if(owner == null || !owner.equals(e.getDamagee())) {
            return;
        }
        if(!e.getIgnoreArmor()) {
            return;
        }
        e.setIgnoreArmor(false);
        e.setDamage(e.getDamage() * damage_multiplier);
    }

}
