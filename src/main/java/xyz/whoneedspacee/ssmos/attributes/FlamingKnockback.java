package xyz.whoneedspacee.ssmos.attributes;

import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerDealSmashDamageEvent;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import org.bukkit.ChatColor;

public class FlamingKnockback extends Attribute implements OwnerDealSmashDamageEvent {

    public double knockback_multiplier;
    public boolean extinguish_after;
    public boolean melee_only;

    public FlamingKnockback(double knockback_multiplier, boolean extinguish_after, boolean melee_only) {
        super();
        this.name = "Flaming Knockback";
        this.knockback_multiplier = knockback_multiplier;
        this.description = new String[] {
                ChatColor.RESET + "When your opponent is burning they",
                ChatColor.RESET + "take bonus knockback from your attacks.",
        };
        this.extinguish_after = extinguish_after;
    }

    public void activate() {
        return;
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(melee_only && e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (e.getDamagee().getFireTicks() <= 0) {
            return;
        }
        e.multiplyKnockback(knockback_multiplier);
        if(extinguish_after) {
            e.getDamagee().setFireTicks(0);
            e.getDamagee().getWorld().playSound(e.getDamagee().getLocation(), Sound.FIZZ, 1.0f, 1.0f);
        }
    }

}
