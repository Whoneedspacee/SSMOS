package SSM.GameManagers.Disguises;

import SSM.Utilities.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerDisguise extends Disguise {

    public PlayerDisguise(Player owner) {
        super(owner);
        name = "Player";
        type = EntityType.PLAYER;
    }

    protected EntityLiving newLiving() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) owner.getWorld()).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "");
        gameProfile.getProperties().clear();
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTA1MzMyMjY3NiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmM4OTBjN2U1OGYyM2U5N2ZmNGRkYWMwNDhiYmZhMDJkNjYxMTEwMTNkMmYxNzdjNWY4ZjYyYThiMWIxYWZkZCIKICAgIH0KICB9Cn0=";
        String signature = "Skp8QvxYFa4YUhw+XHgicva/8j3gnCcN9u0EDLpkSlVCWuYqUeETbgA3LAit4ftjDjNpUA40UPTvlebBsnjcUvEkiu765BvZE61yms2IcNeK7vDoLoeNfx+UqTAquMI6uOzBBNZi6yBeMSghRX2hwVGsiKuzoFb67o1HfFcPLfCOVR3QRd2D84VfduhQmc+MVSFrUhZFGluzOvslUzR8/tbi2ZarkURlLOlgT0UoT1yEX/pHM7GogtnQiJL7xOqfEnU0Ex+OKZYkjawatbD/L5bjbL1pV2QeuxZLrnvRoQFTVAnONhvPfd9f8WF0kdR8DaDe4Knq+SPJ357HOun9ZRci3RobXcyQaRsw5JezSrgUbBccoUr7SiSgdM4VBhtzGGZ8TYUBz5pocHYCaOALG71bZZ4aVjQKfw5Rtalj+q2Wqbub20IQd/7/z9NUvPB0d7zHBLqr8a1UtZoSKLbaVJZJaYqt0ygxff68MKKQlE0L4fupBHEIXdNgza8tp472rsB+o45IZ/xmFltH1jhRsYvV973ki0l4S6U/O6gWu699sUyHn4a3DnVNN0GIyNAIP9KpHhvQzvxPxJq0Z2gXw2rzRGDxt+fe8gYZJ+UF4t/i39IP9RBgryocdu0L0lzeQA0b7vrr1khvAHyBVuZJ0t2S/RHTnlcAcAxoDENP1Gk=";
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
        EntityPlayer entityPlayer = new EntityPlayer(server, world,
                gameProfile, new PlayerInteractManager(world));
        entityPlayer.playerConnection = new PlayerConnection(server, new DummyNetworkManager(EnumProtocolDirection.CLIENTBOUND), entityPlayer);
        entityPlayer.setLocation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        return entityPlayer;
    }

    @Override
    public void showDisguise(Player player) {
        if (player.equals(owner)) {
            return;
        }
        super.showDisguise(player);
        EntityPlayer entityPlayer = (EntityPlayer) living;
        PacketPlayOutPlayerInfo playerInfoAdd = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        PacketPlayOutNamedEntitySpawn namedEntitySpawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);
        PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation(entityPlayer, (byte) ((entityPlayer.yaw * 256f) / 360f));
        PacketPlayOutPlayerInfo playerInfoRemove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);
        Utils.sendPacket(player, playerInfoAdd);
        Utils.sendPacket(player, namedEntitySpawn);
        Utils.sendPacket(player, headRotation);
        Utils.sendPacket(player, playerInfoRemove);
    }

    public static class DummyNetworkManager extends NetworkManager {

        public DummyNetworkManager(EnumProtocolDirection enumprotocoldirection) {
            super(enumprotocoldirection);
        }

    }

}
