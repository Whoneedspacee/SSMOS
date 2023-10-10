package SSM.GameManagers.Disguise;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SkeletonDisguise extends Disguise {

    public SkeletonDisguise(Player owner) {
        super(owner);
        name = "Skeleton";
        type = EntityType.SKELETON;
    }

    public EntityLiving newLiving() {
        return new EntitySkeleton(((CraftWorld) owner.getWorld()).getHandle());
    }

}
