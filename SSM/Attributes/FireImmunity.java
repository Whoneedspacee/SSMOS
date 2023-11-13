package SSM.Attributes;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireImmunity extends Attribute {

    public FireImmunity() {
        super();
        this.name = "Fire Immunity";
        task = this.runTaskTimer(plugin, 0, 0);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        owner.setFireTicks(-20);
    }

}
