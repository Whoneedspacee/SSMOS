package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class Disguise {

    protected String name;
    protected EntityType type;
    protected Player owner;
    protected EntityLiving living;
    protected EntityArmorStand armorstand;
    protected EntitySquid squid;
    protected boolean showAttackAnimation = true;

    public Disguise(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public EntityLiving getLiving() {
        return living;
    }

    public EntityArmorStand getArmorStand() {
        return armorstand;
    }

    public EntitySquid getSquid() {
        return squid;
    }

    public void spawnLiving() {
        if (living != null) {
            deleteLiving();
        }
        living = newLiving();
        armorstand = new EntityArmorStand(((CraftWorld) owner.getWorld()).getHandle());
        armorstand.setCustomName(ChatColor.YELLOW + owner.getName());
        armorstand.setCustomNameVisible(true);
        // Had no idea this existed, but seems like this is what other servers must be doing
        ((CraftArmorStand) armorstand.getBukkitEntity()).setMarker(true);
        squid = new EntitySquid(((CraftWorld) owner.getWorld()).getHandle());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(owner)) {
                continue;
            }
            showDisguise(player);
        }
    }

    public void showDisguise(Player player) {
        // Do not show disguise to self
        if (player.equals(owner)) {
            return;
        }
        // Armor Stand Destroy (if player sees them already)
        PacketPlayOutEntityDestroy destroy_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        Utils.sendPacket(player, destroy_packet);
        // Squid Destroy (if player sees them already)
        destroy_packet = new PacketPlayOutEntityDestroy(squid.getId());
        Utils.sendPacket(player, destroy_packet);
        // Living Destroy (if player sees them already)
        destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(player, destroy_packet);
        // Living Spawn
        living.setPositionRotation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving living_packet = new PacketPlayOutSpawnEntityLiving(living);
        Utils.sendPacket(player, living_packet);
        // Squid Spawn
        squid.setPositionRotation(owner.getLocation().getX(), living.locY + living.an() + squid.am(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving squid_packet = new PacketPlayOutSpawnEntityLiving(squid);
        Utils.sendPacket(player, squid_packet);
        // Armor Stand Spawn
        armorstand.setPositionRotation(owner.getLocation().getX(), squid.locY + squid.an() + armorstand.am(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving armorstand_packet = new PacketPlayOutSpawnEntityLiving(armorstand);
        Utils.sendPacket(player, armorstand_packet);
        // Invisibility for Armor Stand
        DataWatcher dw = armorstand.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        PacketPlayOutEntityMetadata invisiblity_packet = new PacketPlayOutEntityMetadata(armorstand.getId(), dw, true);
        Utils.sendPacket(player, invisiblity_packet);
        // Invisibility for Squid
        dw = squid.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        invisiblity_packet = new PacketPlayOutEntityMetadata(squid.getId(), dw, true);
        Utils.sendPacket(player, invisiblity_packet);
        update();
    }

    public void update() {
        Location location = owner.getLocation();
        // Hide the disguised player from other players
        // Don't use HidePlayer here, it stops the melees
        // But also stops the server from recognizing projectile hits like arrows
        PacketPlayOutEntityDestroy destroy_packet = new PacketPlayOutEntityDestroy(owner.getEntityId());
        Utils.sendPacketToAllBut(owner, destroy_packet);
        // Hide the armor and weapon too
        PacketPlayOutEntityEquipment handPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 0, null);
        PacketPlayOutEntityEquipment helmetPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 1, null);
        PacketPlayOutEntityEquipment chestPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 2, null);
        PacketPlayOutEntityEquipment legPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 3, null);
        PacketPlayOutEntityEquipment bootsPacket = new PacketPlayOutEntityEquipment(owner.getEntityId(), 4, null);
        Utils.sendPacketToAllBut(owner, handPacket);
        Utils.sendPacketToAllBut(owner, helmetPacket);
        Utils.sendPacketToAllBut(owner, chestPacket);
        Utils.sendPacketToAllBut(owner, legPacket);
        Utils.sendPacketToAllBut(owner, bootsPacket);
        // Hide the mob from the disguised player
        PacketPlayOutEntityDestroy disguise_destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(owner, disguise_destroy_packet);
        // Don't teleport to spectator player if the mob is dead
        if (living.dead) {
            return;
        }
        living.onGround = Utils.entityIsOnGround(owner);
        living.setPositionRotation(location.getX(), location.getY(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutEntityTeleport teleport_packet = new PacketPlayOutEntityTeleport(living);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        PacketPlayOutEntityHeadRotation head_packet = new PacketPlayOutEntityHeadRotation(living,
                (byte) ((location.getYaw() * 256.0F) / 360.0F));
        Utils.sendPacketToAllBut(owner, head_packet);
        // From living.mount source code all the way to Entity.class mount
        // In the Entity.class al() method appears to be where it sets the passengers position
        squid.setPositionRotation(location.getX(), living.locY + living.an() + squid.am(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(squid);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        armorstand.setPositionRotation(location.getX(), squid.locY + squid.an() + armorstand.am(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(armorstand);
        Utils.sendPacketToAllBut(owner, teleport_packet);
    }

    public void deleteLiving() {
        if (living == null) {
            return;
        }
        PacketPlayOutEntityDestroy destroy_living_packet = new PacketPlayOutEntityDestroy(living.getId());
        PacketPlayOutEntityDestroy destroy_armorstand_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        PacketPlayOutEntityDestroy destroy_squid_packet = new PacketPlayOutEntityDestroy(squid.getId());
        for (Player player : Bukkit.getOnlinePlayers()) {
            Utils.sendPacket(player, destroy_living_packet);
            Utils.sendPacket(player, destroy_armorstand_packet);
            Utils.sendPacket(player, destroy_squid_packet);
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.showPlayer(owner);
        }
        living = null;
        armorstand = null;
        squid = null;
    }

    protected abstract EntityLiving newLiving();

    public boolean getShowAttackAnimation() {
        return showAttackAnimation;
    }

}
