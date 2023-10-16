package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class ZombieDisguise extends Disguise {

    public ZombieDisguise(Player owner) {
        super(owner);
        name = "Zombie";
        type = EntityType.ZOMBIE;
    }

    protected EntityMonster newLiving() {
        return new EntityZombie(((CraftWorld) owner.getWorld()).getHandle());
    }

    @Override
    public void update() {
        if(living == null) {
            return;
        }
        PacketPlayOutEntityEquipment weapon_packet = new PacketPlayOutEntityEquipment(living.getId(), 0, CraftItemStack.asNMSCopy(owner.getItemInHand()));
        Utils.sendPacketToAllBut(owner, weapon_packet);
        super.update();
    }

}
