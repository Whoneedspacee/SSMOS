package ssm.managers.disguises;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ssm.utilities.Utils;

public class ElderGuardianDisguise extends GuardianDisguise {

    public ElderGuardianDisguise(Player owner) {
        super(owner);
        name = "Guardian";
        type = EntityType.GUARDIAN;
    }

    protected EntityLiving newLiving() {
        EntityGuardian guardian = (EntityGuardian) super.newLiving();
        guardian.setElder(true);
        return guardian;
    }

}
