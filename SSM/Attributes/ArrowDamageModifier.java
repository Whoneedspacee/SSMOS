package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;

public class ArrowDamageModifier extends Attribute implements OwnerDealSmashDamageEvent {

    protected double damage_modifier;
    protected SmashDamageEvent latest;

    public ArrowDamageModifier(double damage_modifier) {
        this.name = "Arrow Damage Modifier";
        this.damage_modifier = damage_modifier;
        this.description = new String[] {
                ChatColor.RESET + "Arrows deal " + String.format("%.1f", damage_modifier) + " modified damage.",
        };
    }

    @Override
    public void activate() {
        return;
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        e.setDamage(e.getDamage() + damage_modifier);
    }

}
