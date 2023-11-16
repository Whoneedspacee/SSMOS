package ssm.attributes;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.ownerevents.OwnerDealSmashDamageEvent;
import org.bukkit.ChatColor;

public class FlamingKnockback extends Attribute implements OwnerDealSmashDamageEvent {

    private double knockback_multiplier;

    public FlamingKnockback(double knockback_multiplier) {
        super();
        this.name = "Flaming Knockback";
        this.knockback_multiplier = knockback_multiplier;
        this.description = new String[] {
                ChatColor.RESET + "When your opponent is burning they",
                ChatColor.RESET + "take bonus knockback from your attacks.",
        };
    }

    public void activate() {
        return;
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if (e.getDamagee().getFireTicks() <= 0) {
            return;
        }
        e.multiplyKnockback(knockback_multiplier);
    }

}
