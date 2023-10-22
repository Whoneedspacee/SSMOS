package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MagmaCubeDisguise extends Disguise {

    public MagmaCubeDisguise(Player owner) {
        super(owner);
        name = "Magma Cube";
        type = EntityType.MAGMA_CUBE;
    }

    protected EntityLiving newLiving() {
        EntityMagmaCube magmaCube = new EntityMagmaCube(((CraftWorld) owner.getWorld()).getHandle());
        magmaCube.setSize(1);
        /*int size = 1;
        DataWatcher dw = magmaCube.getDataWatcher();
        dw.watch(16, (byte) size);
        PacketPlayOutEntityMetadata size_packet = new PacketPlayOutEntityMetadata(magmaCube.getId(), dw, true);
        Utils.sendPacketToAll(size_packet);*/
        return magmaCube;
    }

}
