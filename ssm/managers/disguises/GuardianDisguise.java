package ssm.managers.disguises;

import org.bukkit.Sound;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
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

    @Override
    public void playDamageSound() {
        for(Player player : owner.getWorld().getPlayers()) {
            player.playSound(owner.getLocation(), "mob.guardian.hit", getVolume(), getPitch());
        }
    }

}
