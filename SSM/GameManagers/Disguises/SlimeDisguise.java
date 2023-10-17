package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SlimeDisguise extends Disguise {

    public SlimeDisguise(Player owner) {
        super(owner);
        name = "Slime";
        type = EntityType.SLIME;
    }

    protected EntityLiving newLiving() {
        return new EntitySlime(((CraftWorld) owner.getWorld()).getHandle());
    }

    @Override
    public void update() {
        if (living == null) {
            return;
        }
        int size = 1;
        if (owner.getExp() > 0.8) {
            size = 3;
        } else if (owner.getExp() > 0.55) {
            size = 2;
        }
        DataWatcher dw = living.getDataWatcher();
        // A is the new index method so we'll use that for our fake datawatcher
        dw.watch(16, (byte) size);
        PacketPlayOutEntityMetadata size_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(size_packet);
        super.update();
    }

}
