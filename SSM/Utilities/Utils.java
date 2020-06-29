package SSM.Utilities;

import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;


public class Utils {
    /**
     * Sends a message to the player which appears over their hotbar (e.g, Cooldown Info, Music Disc Info...)
     *
     * @param message message being sent to the player
     * @param player  player receiving the message
     */
    public static void sendActionBarMessage(String message, Player player) {
        String jsonMessage = "{\"text\": \"" + message + "\"}";
        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(jsonMessage);
        PacketPlayOutChat chatPacket = new PacketPlayOutChat(chatBaseComponent, ChatMessageType.GAME_INFO);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(chatPacket);
    }

    public static void sendServerMessageToPlayer(String message, Player player, ServerMessageType type) {
        player.sendMessage(type + " " + message);
    }

    public static boolean holdingItemWithName(Player player, String name) {
        ItemMeta itemMeta = player.getInventory().getItemInMainHand().getItemMeta();
        if (itemMeta != null) {
            return itemMeta.getDisplayName().equals(name);
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
}
