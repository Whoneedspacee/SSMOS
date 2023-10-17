package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityWitch;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class WitchDisguise extends Disguise {

    public WitchDisguise(Player owner) {
        super(owner);
        name = "Witch";
        type = EntityType.WITCH;
    }

    protected EntityLiving newLiving() {
        return new EntityWitch(((CraftWorld) owner.getWorld()).getHandle());
    }

}
