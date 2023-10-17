package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SheepDisguise extends Disguise {

    public SheepDisguise(Player owner) {
        super(owner);
        name = "Sheep";
        type = EntityType.SHEEP;
    }

    protected EntityLiving newLiving() {
        return new EntitySheep(((CraftWorld) owner.getWorld()).getHandle());
    }

}
