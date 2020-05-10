package SSM.Abilities;

import SSM.Ability;
import SSM.Attributes.DoubleJumps.DoubleJump;
import SSM.SSM;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class heroFly extends Ability {

    public heroFly(){
        super();
        this.name = "Zoom";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.FEATHER;
        this.expUsed = 0.5F;
        this.usesEnergy = true;
    }


    public void activate() {
        owner.setVelocity(new Vector(0, 5, 0));
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }
    }
}



