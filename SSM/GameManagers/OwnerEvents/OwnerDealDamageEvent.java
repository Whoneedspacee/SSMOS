package SSM.GameManagers.OwnerEvents;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface OwnerDealDamageEvent {

    public abstract void onOwnerDealDamage(EntityDamageByEntityEvent e);

}
