package ssm.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final short old_state;
    private final short new_state;

    public GameStateChangeEvent(short old_state, short new_state) {
        this.old_state = old_state;
        this.new_state = new_state;
    }

    public short getOldState() {
        return old_state;
    }

    public short getNewState() {
        return new_state;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
