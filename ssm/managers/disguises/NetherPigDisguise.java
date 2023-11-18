package ssm.managers.disguises;

import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EntityMonster;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class NetherPigDisguise extends Disguise {

    public NetherPigDisguise(Player owner) {
        super(owner);
        name = "Zombie";
        type = EntityType.PIG_ZOMBIE;
    }

    protected EntityMonster newLiving() {
        return new EntityPigZombie(((CraftWorld) owner.getWorld()).getHandle());
    }

    @Override
    public void update() {
        if (living == null) {
            return;
        }
        PacketPlayOutEntityEquipment weapon_packet = new PacketPlayOutEntityEquipment(living.getId(), 0, CraftItemStack.asNMSCopy(owner.getItemInHand()));
        Utils.sendPacketToAllBut(owner, weapon_packet);
        super.update();
    }

}
