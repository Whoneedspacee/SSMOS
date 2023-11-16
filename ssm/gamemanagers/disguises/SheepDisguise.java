package ssm.gamemanagers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SheepDisguise extends Disguise {

    protected int color_id = 0;

    public SheepDisguise(Player owner) {
        super(owner);
        name = "Sheep";
        type = EntityType.SHEEP;
    }

    protected EntityLiving newLiving() {
        return new EntitySheep(((CraftWorld) owner.getWorld()).getHandle());
    }

    public void setColor(DyeColor color) {
        setColor(color.ordinal());
    }

    public void setColor(int color_id) {
        this.color_id = color_id;
        DataWatcher dw = living.getDataWatcher();
        dw.watch(16, (byte) color_id);
        PacketPlayOutEntityMetadata target_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(target_packet);
    }

    public int getColor() {
        return color_id;
    }

    public void setSheared() {
        setColor(16);
    }

    public boolean getSheared() {
        return (color_id == 16);
    }

}
