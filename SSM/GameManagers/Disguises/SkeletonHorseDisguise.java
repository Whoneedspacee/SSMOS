package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SkeletonHorseDisguise extends Disguise {

    public SkeletonHorseDisguise(Player owner) {
        super(owner);
        name = "Sheep";
        type = EntityType.HORSE;
    }

    protected EntityLiving newLiving() {
        EntityHorse horse = new EntityHorse(((CraftWorld) owner.getWorld()).getHandle());
        horse.setType(4);
        return horse;
    }

    public void setRearing(boolean rearing) {
        DataWatcher dw = living.getDataWatcher();
        int mask_value = 0;
        if(rearing) {
            mask_value = 0x40;
        }
        dw.watch(16, mask_value);
        PacketPlayOutEntityMetadata rearing_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(rearing_packet);
    }

}
