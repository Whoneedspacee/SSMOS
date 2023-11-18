package ssm.managers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class MagmaCubeDisguise extends Disguise {

    protected int size = 1;

    public MagmaCubeDisguise(Player owner) {
        super(owner);
        name = "Magma Cube";
        type = EntityType.MAGMA_CUBE;
    }

    protected EntityLiving newLiving() {
        EntityMagmaCube magmaCube = new EntityMagmaCube(((CraftWorld) owner.getWorld()).getHandle());
        magmaCube.setSize(size);
        return magmaCube;
    }

    @Override
    public void update() {
        if (living == null) {
            return;
        }
        DataWatcher dw = living.getDataWatcher();
        dw.watch(16, (byte) size);
        PacketPlayOutEntityMetadata size_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(size_packet);
        super.update();
    }

    public void setSize(int size) {
        this.size = size;
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
