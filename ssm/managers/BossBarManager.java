package ssm.managers;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ssm.Main;
import ssm.utilities.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager implements Listener {

    public static BossBarManager ourInstance;
    private static JavaPlugin plugin = Main.getInstance();
    private static Map<Player, EntityLiving> bar_first = new ConcurrentHashMap<>();
    private static Map<Player, EntityLiving> bar_second = new ConcurrentHashMap<>();

    public BossBarManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static void setBar(Player player, String text, float healthPercent) {
        Location loc = player.getLocation();
        WorldServer world = ((CraftWorld) player.getLocation().getWorld()).getHandle();
        // Front
        EntityLiving first_mob = new EntityWither(world);
        first_mob.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(first_mob);
        DataWatcher watcher = new DataWatcher(first_mob);
        watcher.a(0, (byte) 0x20);
        watcher.a(6, (healthPercent * 300) / 100);
        watcher.a(10, text);
        watcher.a(2, text);
        watcher.a(11, (byte) 1);
        watcher.a(3, (byte) 1);
        try {
            Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            t.setAccessible(true);
            t.set(packet, watcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        bar_first.put(player, first_mob);
        Utils.sendPacket(player, packet);
        // Back
        EntityLiving second_mob = new EntityWither(world);
        second_mob.setLocation(loc.getX(), loc.getY(), loc.getZ(), 0, 0);
        packet = new PacketPlayOutSpawnEntityLiving(second_mob);
        watcher = new DataWatcher(second_mob);
        watcher.a(0, (byte) 0x20);
        watcher.a(6, (healthPercent * 300) / 100);
        watcher.a(10, text);
        watcher.a(2, text);
        watcher.a(11, (byte) 1);
        watcher.a(3, (byte) 1);
        try {
            Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            t.setAccessible(true);
            t.set(packet, watcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        bar_second.put(player, second_mob);
        Utils.sendPacket(player, packet);
    }

    public static void removeBar(Player player) {
        if (bar_first.containsKey(player)) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(bar_first.get(player).getId());
            bar_first.remove(player);
            Utils.sendPacket(player, packet);
        }
        if (bar_second.containsKey(player)) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(bar_second.get(player).getId());
            bar_second.remove(player);
            Utils.sendPacket(player, packet);
        }
    }

    public static void teleportBar(Player player) {
        if (bar_first.containsKey(player)) {
            Location location = player.getLocation();
            location.add(player.getLocation().getDirection().multiply(-30));
            bar_first.get(player).setPositionRotation(location.getX(), location.getY(), location.getZ(),
                    location.getYaw(), location.getPitch());
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(bar_first.get(player));
            Utils.sendPacket(player, packet);
        }
        if (bar_second.containsKey(player)) {
            Location location = player.getLocation();
            location.add(player.getLocation().getDirection().multiply(30));
            bar_second.get(player).setPositionRotation(location.getX(), location.getY(), location.getZ(),
                    location.getYaw(), location.getPitch());
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(bar_second.get(player));
            Utils.sendPacket(player, packet);
        }
    }

    public static void updateText(Player player, String text) {
        updateBar(player, text, -1);
    }

    public static void updateHealth(Player player, float healthPercent) {
        updateBar(player, null, healthPercent);
    }

    public static void updateBar(Player player, String text, float healthPercent) {
        if (!bar_first.containsKey(player) || !bar_second.containsKey(player)) {
            removeBar(player);
            setBar(player, text, healthPercent);
        }
        if (bar_first.containsKey(player)) {
            DataWatcher watcher = new DataWatcher(bar_first.get(player));
            watcher.a(0, (byte) 0x20);
            if (healthPercent != -1) watcher.a(6, (healthPercent * 300) / 100);
            if (text != null) {
                watcher.a(10, text);
                watcher.a(2, text);
            }
            watcher.a(11, (byte) 1);
            watcher.a(3, (byte) 1);

            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(bar_first.get(player).getId(), watcher, true);
            Utils.sendPacket(player, packet);
            // Keep the entity loaded by sending status 0 packets repeatedly
            PacketPlayOutEntityStatus status_packet = new PacketPlayOutEntityStatus(bar_first.get(player), (byte) 0);
            Utils.sendPacket(player, status_packet);
        }
        if (bar_second.containsKey(player)) {
            DataWatcher watcher = new DataWatcher(bar_second.get(player));
            watcher.a(0, (byte) 0x20);
            if (healthPercent != -1) watcher.a(6, (healthPercent * 300) / 100);
            if (text != null) {
                watcher.a(10, text);
                watcher.a(2, text);
            }
            watcher.a(11, (byte) 1);
            watcher.a(3, (byte) 1);

            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(bar_second.get(player).getId(), watcher, true);
            Utils.sendPacket(player, packet);
            // Keep the entity loaded by sending status 0 packets repeatedly
            PacketPlayOutEntityStatus status_packet = new PacketPlayOutEntityStatus(bar_second.get(player), (byte) 0);
            Utils.sendPacket(player, status_packet);
        }
    }

    public static Set<Player> getPlayers() {
        Set<Player> set = new HashSet<>();

        for (Map.Entry<Player, EntityLiving> entry : bar_first.entrySet()) {
            set.add(entry.getKey());
        }

        return set;
    }

}
