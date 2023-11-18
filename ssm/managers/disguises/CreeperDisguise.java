package ssm.managers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class CreeperDisguise extends Disguise {

    private byte current_fuse_state = -1;
    private byte current_powered_state = 0;

    public CreeperDisguise(Player owner) {
        super(owner);
        name = "Creeper";
        type = EntityType.CREEPER;
    }

    protected EntityLiving newLiving() {
        return new EntityCreeper(((CraftWorld) owner.getWorld()).getHandle());
    }

    @Override
    public void update() {
        if(living == null) {
            return;
        }
        // Do these in update and track them so that even if the disguise gets re-shown it will be correct
        // If you wanted to reduce packets sent you could put these in ShowDisguise more than likely
        // Unfortunately packet code scares me
        DataWatcher dw = living.getDataWatcher();
        dw.watch(16, current_fuse_state);
        dw.watch(17, current_powered_state);
        PacketPlayOutEntityMetadata data_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(data_packet);
        super.update();
    }

    public void setFuseState(byte value) {
        if (living == null) {
            return;
        }
        current_fuse_state = value;
        update();
    }

    public void setPoweredState(byte value) {
        if (living == null) {
            return;
        }
        current_powered_state = value;
        update();
    }

}
