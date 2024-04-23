package xyz.whoneedspacee.ssmos.managers.disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPig;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PigDisguise extends Disguise {

    public PigDisguise(Player owner) {
        super(owner);
        name = "Pig";
        type = EntityType.PIG;
    }

    protected EntityLiving newLiving() {
        return new EntityPig(((CraftWorld) owner.getWorld()).getHandle());
    }

}
