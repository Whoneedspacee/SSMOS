package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Material;
import org.bukkit.entity.DragonFireball;


public class heroFireball extends Ability {

    public heroFireball(){
        this.name = "Dragon Fireball";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.FIRE_CHARGE;
        this.expUsed = 0.35F;
        this.usesEnergy = true;
    }

    public void activate() {
        owner.launchProjectile(DragonFireball.class);
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }
    }
}
