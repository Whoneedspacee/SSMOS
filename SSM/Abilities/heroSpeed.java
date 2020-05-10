package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class heroSpeed extends Ability {

    public heroSpeed(){
        super();
        this.name = "Accelerate";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.SUGAR;
        this.expUsed = 0.2F;
        this.usesEnergy = true;

    }
    public void activate() {
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 180, 5));
        owner.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 180, 5));
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }
    }
}
