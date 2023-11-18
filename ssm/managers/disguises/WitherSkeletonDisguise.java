package ssm.managers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class WitherSkeletonDisguise extends Disguise {

    public WitherSkeletonDisguise(Player owner) {
        super(owner);
        name = "Wither Skeleton";
        type = EntityType.SKELETON;
    }

    protected EntityLiving newLiving() {
        EntitySkeleton skeleton = new EntitySkeleton(((CraftWorld) owner.getWorld()).getHandle());
        skeleton.setSkeletonType(1);
        return skeleton;
    }

    @Override
    public void update() {
        if (living == null) {
            return;
        }
        PacketPlayOutEntityEquipment weapon_packet = new PacketPlayOutEntityEquipment(living.getId(), 0, CraftItemStack.asNMSCopy(owner.getItemInHand()));
        Utils.sendPacketToAllBut(owner, weapon_packet);
        super.update();
    }

}
