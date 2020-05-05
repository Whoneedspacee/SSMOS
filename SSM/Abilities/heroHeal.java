package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Material;
import org.bukkit.Particle;

public class heroHeal extends Ability {

    public heroHeal(){
        this.name = "Heal";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.COOKED_BEEF;
        this.expUsed = 0.45F;
        this.usesEnergy = true;
    }

    public void activate() {
        owner.setHealth(owner.getHealth()+10);
        owner.getWorld().spawnParticle(Particle.HEART, owner.getLocation(), 5);
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }
    }
}
