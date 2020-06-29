package SSM.Attributes;

import SSM.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

;

public class Heavyweight extends Attribute {

    public Heavyweight() {
        super();
        this.name = "Heavyweight";
        task = this.runTaskTimer(plugin, 0, 60);
    }

    @Override
    public void run() {
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 0));
    }
}
