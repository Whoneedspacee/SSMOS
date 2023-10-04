package SSM.GameManagers.OwnerEvents;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageOwnerEvent {

    public abstract void onEntityDamageOwner(EntityDamageByEntityEvent e);

}
