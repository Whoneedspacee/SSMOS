package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerTakeSmashDamageEvent;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LightningShield extends Attribute implements OwnerTakeSmashDamageEvent {

    /*
     - - Lightning Shield
      - - Duration
        - '2'
      - - Shock
        - '1'
      - - Damage
        - '4'
      - - Knockback Magnitude
        - '2.5'
     */

    public LightningShield() {
        super();
        this.name = "Lightning Shield";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "When attacked by a non-melee attack,",
                ChatColor.RESET + "you gain Lightning Shield for 2 seconds.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Lightning Shield blocks 1 melee attack,",
                ChatColor.RESET + "striking lightning on the attacker.",
        };
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        return;
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {

    }
}
