package SSM.Utilities;

import SSM.Attributes.Attribute;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;

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

    public static void sendTitleMessage(Player player, String title_string, String subtitle_string) {
        sendTitleMessage(player, title_string, subtitle_string, 20, 60, 20);
    }

    public static void sendTitleMessage(Player player, String title_string, String subtitle_string,
                                        int fade_in_ticks, int stay_ticks, int fade_out_ticks) {
        PacketPlayOutTitle timing_packet = new PacketPlayOutTitle(fade_in_ticks, stay_ticks, fade_out_ticks);
        Utils.sendPacket(player, timing_packet);
        ChatMessage subtitle_message = new ChatMessage(subtitle_string);
        PacketPlayOutTitle subtitle_packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle_message);
        Utils.sendPacket(player, subtitle_packet);
        ChatMessage title_message = new ChatMessage(title_string);
        PacketPlayOutTitle title_packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title_message);
        Utils.sendPacket(player, title_packet);
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

    public static HashMap<LivingEntity, Double> getInRadius(Location location, double radius) {
        HashMap<LivingEntity, Double> ents = new HashMap<>();
        for (Entity cur : location.getWorld().getEntities()) {
            if (!(cur instanceof LivingEntity)) {
                continue;
            }
            LivingEntity ent = (LivingEntity)cur;
            if(!DamageUtil.canDamage(ent, null, 0)) {
                continue;
            }
            //Feet
            double offset = location.distance(ent.getLocation());
            if (offset < radius) {
                ents.put(ent, 1 - (offset / radius));
                continue;
            }
            //Eyes
            offset = location.distance(ent.getEyeLocation());
            if (offset < radius) {
                ents.put(ent, 1 - (offset / radius));
            }
        }
        return ents;
    }

    public static void fullHeal(LivingEntity livingEntity) {
        livingEntity.setHealth(livingEntity.getMaxHealth());
        livingEntity.setFireTicks(0);
        if(livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0);
        }
    }

    public static double getXZDistance(Location first, Location second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getZ() - second.getZ(), 2));
    }

    public static void playParticle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ,
                                    float speed, int count, int dist, Collection<Player> players) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                offsetX, offsetY, offsetZ, speed, count, dist);

        for (Player player : players) {
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
