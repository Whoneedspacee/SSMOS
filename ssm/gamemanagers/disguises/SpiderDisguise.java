package ssm.gamemanagers.disguises;

import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySpider;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpiderDisguise extends Disguise {

    public SpiderDisguise(Player owner) {
        super(owner);
        name = "Spider";
        type = EntityType.SPIDER;
    }

    protected EntityLiving newLiving() {
        return new EntitySpider(((CraftWorld) owner.getWorld()).getHandle());
    }

}
