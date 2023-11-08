package SSM.GameManagers.OwnerEvents;

import SSM.Events.SmashDamageEvent;
import org.bukkit.entity.Player;

public interface OwnerKillEvent {

    public abstract void onOwnerKillEvent(Player player);

}
