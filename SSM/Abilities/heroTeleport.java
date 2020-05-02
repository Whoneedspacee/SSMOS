package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class heroTeleport extends Ability {

    public heroTeleport(){
        this.name = "Teleport";
        this.cooldownTime = 0;
        this.item = Material.POPPED_CHORUS_FRUIT;
        this.rightClickActivate = true;
        this.expUsed = 0.5F;
        this.usesEnergy = true;
    }


    public void activate() {
        for (int i = 1; i <= 40; i++){
            Location loc = owner.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(i);
            loc.add(dir);
            if (loc.getBlock().getType().isAir()){
                continue;
            }else{
                loc = owner.getLocation();
                dir = loc.getDirection();
                dir.normalize();
                dir.multiply(i-2);
                loc.add(dir);
                owner.teleport(loc);
            }
        }
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }

    }
}
