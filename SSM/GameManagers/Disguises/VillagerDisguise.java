package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.EntityVillager;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class VillagerDisguise extends Disguise {

    public VillagerDisguise(Player owner) {
        super(owner);
        name = "Villager";
        type = EntityType.VILLAGER;
    }

    protected EntityLiving newLiving() {
        return new EntityVillager(((CraftWorld) owner.getWorld()).getHandle());
    }

}
