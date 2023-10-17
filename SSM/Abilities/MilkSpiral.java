package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MilkSpiral extends Ability implements OwnerRightClickEvent {

    // Block Range
    private double range = 7;

    public MilkSpiral() {
        super();
        this.name = "Sonic Hurr";
        this.cooldownTime = 0.5;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        World world = owner.getWorld();
        Location ownerLoc = owner.getEyeLocation();
        Vector dir = ownerLoc.getDirection().normalize();
        Vector ownerPos = ownerLoc.toVector().add(dir.clone());
        Vector v1 = new Vector(-dir.getZ(), 0, dir.getX()).normalize();
        Vector v2 = dir.getCrossProduct(v1);
        world.playSound(ownerLoc, Sound.VILLAGER_IDLE, 1.0F, 1.0F);
        double i = 0;
        double theta = 0;
        while (i < range) {
            // Full rotation happens every 4 blocks, which means
            // when i += 4, theta += 2PI, so i += 1 means theta += PI/2
            i += 1.0 / 20;
            theta += (Math.PI / 2) / 20;
            Vector cone_pos = v1.clone().multiply(Math.cos(theta)).add(v2.clone().multiply(Math.sin(theta))).normalize().multiply(Math.min(i, 2));
            Vector opposite_cone_pos = cone_pos.clone().multiply(-1);
            Vector world_cone_pos = ownerPos.clone().add(dir.clone().multiply(i)).add(cone_pos);
            Vector opposite_world_cone_pos = ownerPos.clone().add(dir.clone().multiply(i)).add(opposite_cone_pos);
            Location first = world_cone_pos.toLocation(world);
            Location second = opposite_world_cone_pos.toLocation(world);
            PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, false,
                    (float) first.getX(), (float) first.getY(), (float) first.getZ(),
                    0, 0, 0,
                    0, 0, 0);
            ((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
            packet = new PacketPlayOutWorldParticles(EnumParticle.CLOUD, false,
                    (float) second.getX(), (float) second.getY(), (float) second.getZ(),
                    0, 0, 0,
                    0, 0, 0);
            ((CraftPlayer) owner).getHandle().playerConnection.sendPacket(packet);
        }
    }

}