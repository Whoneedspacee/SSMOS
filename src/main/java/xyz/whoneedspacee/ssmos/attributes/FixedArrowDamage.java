package xyz.whoneedspacee.ssmos.attributes;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerDealSmashDamageEvent;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;

public class FixedArrowDamage extends Attribute implements OwnerDealSmashDamageEvent {

    protected double fixed_damage;
    protected SmashDamageEvent latest;

    public FixedArrowDamage(double fixed_damage) {
        this.name = "Fixed Arrow Damage";
        this.fixed_damage = fixed_damage;
        this.description = new String[] {
                ChatColor.RESET + "Arrows deal " + String.format("%.1f", fixed_damage) + " damage.",
        };
    }

    @Override
    public void activate() {
        latest.setDamage(fixed_damage);
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        latest = e;
        checkAndActivate();
    }

}
