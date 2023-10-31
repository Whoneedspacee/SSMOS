package SSM.GameManagers.OwnerEvents;

import SSM.Events.SmashDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface OwnerDealSmashDamageEvent {

    public abstract void onOwnerDealSmashDamageEvent(SmashDamageEvent e);

}
