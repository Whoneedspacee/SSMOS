package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ChickenDisguise extends Disguise {

    public ChickenDisguise(Player owner) {
        super(owner);
        name = "Chicken";
        type = EntityType.CHICKEN;
    }

    protected EntityLiving newLiving() {
        return new EntityChicken(((CraftWorld) owner.getWorld()).getHandle());
    }

}
