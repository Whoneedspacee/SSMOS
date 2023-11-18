package ssm.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ssm.managers.smashserver.SmashServer;

public class GameStateChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final SmashServer server;
    private final short old_state;
    private final short new_state;

    public GameStateChangeEvent(SmashServer server, short old_state, short new_state) {
        this.server = server;
        this.old_state = old_state;
        this.new_state = new_state;
    }

    public SmashServer getServer() {
        return server;
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
