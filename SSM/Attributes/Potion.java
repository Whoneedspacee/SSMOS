package SSM.Attributes;

import org.bukkit.potion.PotionEffectType;

public class Potion extends Attribute {

    private PotionEffectType effect;
    private int level;

    public Potion(PotionEffectType effect, int level) {
        super();
        this.name = "Potion";
        this.level = level;
        this.effect = effect;
        task = this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        owner.addPotionEffect(new org.bukkit.potion.PotionEffect(effect, 70, level - 1));
    }

}
