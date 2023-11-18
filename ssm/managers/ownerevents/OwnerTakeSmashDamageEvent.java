package ssm.managers.ownerevents;

import ssm.events.SmashDamageEvent;

public interface OwnerTakeSmashDamageEvent {

    public abstract void onOwnerTakeSmashDamageEvent(SmashDamageEvent e);

}
