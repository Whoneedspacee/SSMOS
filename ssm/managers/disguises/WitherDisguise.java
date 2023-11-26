package ssm.managers.disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntityWither;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class WitherDisguise extends Disguise {

    public WitherDisguise(Player owner) {
        super(owner);
        name = "Wither";
        type = EntityType.WITHER;
    }

    protected EntityLiving newLiving() {
        return new EntityWither(((CraftWorld) owner.getWorld()).getHandle());
    }

}
