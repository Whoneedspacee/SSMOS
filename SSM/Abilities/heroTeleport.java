package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
        Vector dir = owner.getLocation().getDirection();
        Block block = owner.getTargetBlockExact(40);
        Location loc = block.getLocation();
        owner.teleport(new Location(owner.getWorld(), loc.getX(), loc.getY()+1, loc.getZ()));
        owner.getLocation().setDirection(dir);
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }

    }
}
