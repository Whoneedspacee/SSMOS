package SSM.GameManagers.Disguise;

import net.minecraft.server.v1_8_R3.EntityMonster;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.swing.text.html.parser.Entity;

public class ZombieDisguise extends Disguise {

    public ZombieDisguise(Player owner) {
        super(owner);
        name = "Zombie";
        type = EntityType.ZOMBIE;
    }

    protected EntityMonster newLiving() {
        return new EntityZombie(((CraftWorld) owner.getWorld()).getHandle());
    }

}
