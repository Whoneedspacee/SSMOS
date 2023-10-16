package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EndermanDisguise extends Disguise {

    public EndermanDisguise(Player owner) {
        super(owner);
        name = "Enderman";
        type = EntityType.ENDERMAN;
    }

    protected EntityLiving newLiving() {
        return new EntityEnderman(((CraftWorld) owner.getWorld()).getHandle());
    }

}
