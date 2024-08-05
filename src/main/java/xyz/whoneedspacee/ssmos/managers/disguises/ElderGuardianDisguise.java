package xyz.whoneedspacee.ssmos.managers.disguises;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

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

    @Override
    public void playDamageSound() {
        for(Player player : owner.getWorld().getPlayers()) {
            player.playSound(owner.getLocation(), "mob.guardian.elder.hit", getVolume(), getPitch());
        }
    }

}
