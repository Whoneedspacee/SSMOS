package ssm.gamemanagers.disguises;

import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class IronGolemDisguise extends Disguise {

    public IronGolemDisguise(Player owner) {
        super(owner);
        name = "Iron Golem";
        type = EntityType.IRON_GOLEM;
    }

    protected EntityLiving newLiving() {
        return new EntityIronGolem(((CraftWorld) owner.getWorld()).getHandle());
    }

}
