package SSM.Utilities;

import SSM.Attributes.Attribute;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class Utils {

    /**
     * Sends a message to the player which appears over their hotbar (e.g, Cooldown Info, Music Disc Info...)
     *
     * @param message message being sent to the player
     * @param player  player receiving the message
     */
    public static void sendActionBarMessage(String message, Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendServerMessageToPlayer(String message, Player player, ServerMessageType type) {
        player.sendMessage(type + " " + message);
    }

    public static void sendAttributeMessage(Attribute attribute, Player player, ServerMessageType type) {
        sendAttributeMessage(attribute.getUseMessage(), attribute.name, player, type);
    }

    public static void sendAttributeMessage(String primary_message, String secondary_message, Player player, ServerMessageType type) {
        sendServerMessageToPlayer("§7" + primary_message + " §a" + secondary_message + "§7.", player, type);
    }

    public static boolean holdingItemWithName(Player player, String name) {
        if (player == null || player.getInventory().getItemInHand() == null) {
            return false;
        }
        ItemMeta itemMeta = player.getInventory().getItemInHand().getItemMeta();
        if (itemMeta != null) {
            String itemname = itemMeta.getDisplayName();
            if (itemname != null) {
                return itemname.equals(name);
            }
        }

        return false;
    }

    /**
     * Convert milliseconds to seconds with one tenth degree of accuracy
     *
     * @return seconds
     */
    public static double msToSeconds(long milliseconds) {
        return (long) (milliseconds / (double) 100) / (double) 10;
    }

    public static boolean entityIsOnGround(Entity ent) {
        if (ent == null) {
            return false;
        }
        World world = ent.getWorld();
        Location location = ent.getLocation();
        double[] coords = {-0.3, 0, 0.3};
        for (double x : coords) {
            for (double z : coords) {
                if (!world.getBlockAt(ent.getLocation().subtract(x, 0.5, z)).getType().isTransparent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double getXZDistance(Location first, Location second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getZ() - second.getZ(), 2));
    }

    public static void playParticle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ,
                                    float speed, int count, int dist, Collection<Player> players) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                offsetX, offsetY, offsetZ, speed, count, dist);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(location) > dist)
                continue;

            Utils.sendPacket(player, packet);
        }
    }

    public static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendPacketToAllBut(Player exclude, Packet packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(exclude)) {
                continue;
            }
            sendPacket(player, packet);
        }
    }

    public static void sendPacketToAll(Packet packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

}
