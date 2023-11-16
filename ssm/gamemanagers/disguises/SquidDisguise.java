package ssm.gamemanagers.disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySquid;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SquidDisguise extends Disguise {

    public SquidDisguise(Player owner) {
        super(owner);
        name = "Squid";
        type = EntityType.SQUID;
    }

    protected EntityLiving newLiving() {
        return new EntitySquid(((CraftWorld) owner.getWorld()).getHandle());
    }

}
