package ssm.managers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class EndermanDisguise extends Disguise {

    private short block_id = 0;
    private byte block_data = 0;

    public EndermanDisguise(Player owner) {
        super(owner);
        name = "Enderman";
        type = EntityType.ENDERMAN;
    }

    protected EntityLiving newLiving() {
        return new EntityEnderman(((CraftWorld) owner.getWorld()).getHandle());
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
        dw.watch(16, block_id);
        dw.watch(17, block_data);
        PacketPlayOutEntityMetadata data_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(data_packet);
        super.update();
    }

    public void setHeldBlock(int id, byte data) {
        block_id = (short) id;
        block_data = data;
        update();
    }

}
