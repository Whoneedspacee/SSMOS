package xyz.whoneedspacee.ssmos.managers.disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SnowmanDisguise extends Disguise {

    public SnowmanDisguise(Player owner) {
        super(owner);
        name = "Snowman";
        type = EntityType.SNOWMAN;
    }

    protected EntityLiving newLiving() {
        return new EntitySnowman(((CraftWorld) owner.getWorld()).getHandle());
    }

}
