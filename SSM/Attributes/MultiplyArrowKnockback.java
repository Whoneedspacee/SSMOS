package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;

public class MultiplyArrowKnockback extends Attribute implements OwnerDealSmashDamageEvent {

    protected double multiplied_knockback;
    protected SmashDamageEvent latest;

    public MultiplyArrowKnockback(double multiplied_knockback) {
        this.name = "Multiply Arrow Knockback";
        this.multiplied_knockback = multiplied_knockback;
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
