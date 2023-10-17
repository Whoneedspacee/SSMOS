package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SkeletonHorseDisguise extends Disguise {

    public SkeletonHorseDisguise(Player owner) {
        super(owner);
        name = "Sheep";
        type = EntityType.SHEEP;
    }

    protected EntityLiving newLiving() {
        EntityHorse horse = new EntityHorse(((CraftWorld) owner.getWorld()).getHandle());
        horse.setType(4);
        return horse;
    }

}
