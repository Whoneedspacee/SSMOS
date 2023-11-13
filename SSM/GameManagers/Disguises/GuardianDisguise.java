package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class GuardianDisguise extends Disguise {

    public GuardianDisguise(Player owner) {
        super(owner);
        name = "Guardian";
        type = EntityType.GUARDIAN;
    }

    protected EntityLiving newLiving() {
        return new EntityGuardian(((CraftWorld) owner.getWorld()).getHandle());
    }

    public void setTarget(Entity entity) {
        this.target = entity;
        int id = -1;
        if(target != null) {
            id = target.getEntityId();
        }
        DataWatcher dw = living.getDataWatcher();
        dw.watch(17, id);
        PacketPlayOutEntityMetadata target_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(target_packet);
    }

}
