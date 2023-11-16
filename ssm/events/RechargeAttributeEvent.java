package ssm.events;

import ssm.attributes.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RechargeAttributeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player owner;
    private final Attribute attribute;

    public RechargeAttributeEvent(Player owner, Attribute attribute) {
        this.owner = owner;
        this.attribute = attribute;
    }

    public Player getOwner() {
        return owner;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
