package SSM.GameManagers.Disguises;

import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class GuardianDisguise extends Disguise {

    public GuardianDisguise(Player owner) {
        super(owner);
        name = "Guardian";
        type = EntityType.GUARDIAN;
    }

    protected EntityLiving newLiving() {
        return new EntityGuardian(((CraftWorld) owner.getWorld()).getHandle());
    }

}
