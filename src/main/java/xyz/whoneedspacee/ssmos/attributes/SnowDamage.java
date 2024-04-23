package xyz.whoneedspacee.ssmos.attributes;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerDealSmashDamageEvent;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import org.bukkit.ChatColor;

public class SnowDamage extends Attribute implements OwnerDealSmashDamageEvent {

    private double bonus_damage = 1;
    private double bonus_knockback_multiplier = 0.4;

    public SnowDamage() {
        super();
        this.name = "Snow Damage";
        this.description = new String[]{
                ChatColor.RESET + "Deal bonus damage and reduced knockback",
                ChatColor.RESET + "to enemies who are standing in snow.",
                ChatColor.RESET + "",
        };
        this.usage = AbilityUsage.PASSIVE;
    }

    public void activate() {
        return;
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(e.getDamagee().getLocation().getBlock().getTypeId() != 78) {
            return;
        }
        e.setDamage(e.getDamage() + bonus_damage);
        e.multiplyKnockback(bonus_knockback_multiplier);
    }

}
