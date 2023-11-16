package ssm.gamemanagers.disguises;

import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CowDisguise extends Disguise {

    public CowDisguise(Player owner) {
        super(owner);
        name = "Cow";
        type = EntityType.COW;
    }

    protected EntityLiving newLiving() {
        return new EntityCow(((CraftWorld) owner.getWorld()).getHandle());
    }

}
