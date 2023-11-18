package ssm.attributes;

import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;

public class MultiplyArrowKnockback extends Attribute implements OwnerDealSmashDamageEvent {

    protected double multiplied_knockback;
    protected SmashDamageEvent latest;

    public MultiplyArrowKnockback(double multiplied_knockback) {
        this.name = "Multiply Arrow Knockback";
        this.multiplied_knockback = multiplied_knockback;
        this.description = new String[] {
                ChatColor.RESET + "Arrows deal " + String.format("%.1f", multiplied_knockback) + "% knockback.",
        };
    }

    @Override
    public void activate() {
        latest.multiplyKnockback(multiplied_knockback);
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
