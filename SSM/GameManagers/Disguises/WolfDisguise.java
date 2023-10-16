package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWolf;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class WolfDisguise extends Disguise {

    public WolfDisguise(Player owner) {
        super(owner);
        name = "Wolf";
        type = EntityType.WOLF;
    }

    protected EntityLiving newLiving() {
        return new EntityWolf(((CraftWorld) owner.getWorld()).getHandle());
    }

}
