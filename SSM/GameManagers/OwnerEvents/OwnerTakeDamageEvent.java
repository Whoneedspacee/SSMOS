package SSM.GameManagers.OwnerEvents;

import org.bukkit.event.entity.EntityDamageEvent;

public interface OwnerTakeDamageEvent {

    public abstract void onOwnerTakeDamage(EntityDamageEvent e);

}
