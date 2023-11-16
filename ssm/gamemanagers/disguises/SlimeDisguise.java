package ssm.gamemanagers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SlimeDisguise extends Disguise {

    protected int size = 1;

    public SlimeDisguise(Player owner) {
        super(owner);
        name = "Slime";
        type = EntityType.SLIME;
    }

    protected EntityLiving newLiving() {
        EntitySlime slime = new EntitySlime(((CraftWorld) owner.getWorld()).getHandle());
        slime.setSize(1);
        return slime;
    }

    @Override
    public void update() {
        if (living == null) {
            return;
        }
        size = 1;
        if (owner.getExp() > 0.8) {
            size = 3;
        } else if (owner.getExp() > 0.55) {
            size = 2;
        }
        DataWatcher dw = living.getDataWatcher();
        dw.watch(16, (byte) size);
        PacketPlayOutEntityMetadata size_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(size_packet);
        super.update();
    }

    @Override
    public Sound getDamageSound() {
        if(size > 1) {
            return Sound.SLIME_WALK2;
        }
        return Sound.SLIME_WALK;
    }

    @Override
    public float getVolume() {
        return 0.4f * (float) size;
    }

}
