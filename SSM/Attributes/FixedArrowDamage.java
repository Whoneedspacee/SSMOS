package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class FixedArrowDamage extends Attribute implements OwnerDealSmashDamageEvent {

    protected double fixed_damage;
    protected SmashDamageEvent latest;

    public FixedArrowDamage(double fixed_damage) {
        this.name = "Fixed Arrow Damage";
        this.fixed_damage = fixed_damage;
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
