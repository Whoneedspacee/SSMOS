package xyz.whoneedspacee.ssmos.managers.disguises;

import xyz.whoneedspacee.ssmos.utilities.Utils;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class VillagerDisguise extends Disguise {

    public VillagerDisguise(Player owner) {
        super(owner);
        name = "Villager";
        type = EntityType.VILLAGER;
    }

    protected EntityLiving newLiving() {
        return new EntityVillager(((CraftWorld) owner.getWorld()).getHandle());
    }

    public void setProfession(int id) {
        DataWatcher dw = living.getDataWatcher();
        dw.watch(16, id);
        PacketPlayOutEntityMetadata profession_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAll(profession_packet);
    }

    public void setFarmer() {
        setProfession(0);
    }

    public void setLibrarian() {
        setProfession(1);
    }

    public void setPriest() {
        setProfession(2);
    }

    public void setBlacksmith() {
        setProfession(3);
    }

    public void setButcher() {
        setProfession(4);
    }

}
