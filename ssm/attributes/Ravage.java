package ssm.attributes;

import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class Ravage extends Attribute implements OwnerDealSmashDamageEvent {

    private List<Long> hit_times_ms = new ArrayList<Long>();
    private long expire_time_ms = 3000;

    public Ravage() {
        super();
        this.name = "Ravage";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "When you attack someone, you receive",
                ChatColor.RESET + "+1 Damage for 3 seconds. Bonus damage",
                ChatColor.RESET + "stacks from multiple hits.",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    public void activate() {
        return;
    }

    @Override
    public void run() {
        if(owner == null) {
            this.cancel();
            return;
        }
        if(!check()) {
            hit_times_ms.clear();
            owner.setExp(0);
            return;
        }
        hit_times_ms.removeIf(hit_time -> (System.currentTimeMillis() - hit_time > expire_time_ms));
        owner.setExp(0.1111111111F * hit_times_ms.size());
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(!check()) {
            return;
        }
        int stacks = hit_times_ms.size();
        e.setDamage(e.getDamage() + Math.min(3, stacks));
        owner.getWorld().playSound(owner.getLocation(), Sound.WOLF_BARK, (float) (0.5 + stacks * 0.25), (float) (1 + stacks * 0.25));
        hit_times_ms.add(System.currentTimeMillis());
        owner.setExp(0.1111111111F * hit_times_ms.size());
    }

}
