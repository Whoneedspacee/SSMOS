package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MagmaCubeDisguise extends Disguise {

    public MagmaCubeDisguise(Player owner) {
        super(owner);
        name = "Magma Cube";
        type = EntityType.MAGMA_CUBE;
    }

    protected EntityLiving newLiving() {
        return new EntityMagmaCube(((CraftWorld) owner.getWorld()).getHandle());
    }

}
