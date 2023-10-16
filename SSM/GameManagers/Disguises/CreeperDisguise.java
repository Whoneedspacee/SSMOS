package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CreeperDisguise extends Disguise {

    public CreeperDisguise(Player owner) {
        super(owner);
        name = "Creeper";
        type = EntityType.CREEPER;
    }

    protected EntityLiving newLiving() {
        return new EntityCreeper(((CraftWorld) owner.getWorld()).getHandle());
    }

}
