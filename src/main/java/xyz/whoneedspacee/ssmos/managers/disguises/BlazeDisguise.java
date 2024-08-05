package xyz.whoneedspacee.ssmos.managers.disguises;

import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BlazeDisguise extends Disguise {

    public BlazeDisguise(Player owner) {
        super(owner);
        name = "Blaze";
        type = EntityType.BLAZE;
    }

    protected EntityLiving newLiving() {
        return new EntityBlaze(((CraftWorld) owner.getWorld()).getHandle());
    }

}
