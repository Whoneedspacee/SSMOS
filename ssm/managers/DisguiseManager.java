package ssm.managers;

import ssm.managers.disguises.Disguise;
import ssm.Main;
import ssm.utilities.Utils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.relation.RelationNotFoundException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class DisguiseManager implements Listener, Runnable {

    private static DisguiseManager ourInstance;
    public static HashMap<Player, Disguise> disguises = new HashMap<Player, Disguise>();
    public static HashMap<Entity, Entity> redirect_melee = new HashMap<Entity, Entity>();
    private JavaPlugin plugin = Main.getInstance();

    // Gives disguises to players
    // Needs to handle and update on:
    // Player Joining
    // Player Leaving
    // Player Respawning (when a player dies we need to reload all disguises for them)
    // Everything else should be handled by kits equipping disguises
    // We just need to make sure they show properly if they exist here
    // This took like a day of constant bug fixing
    // and google reading, 0/10 don't bother touching this - Whoneedspacee
    public DisguiseManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        Bukkit.getScheduler().runTaskTimer(plugin, this, 0L, 0L);
        for (Player player : Bukkit.getOnlinePlayers()) {
            initializePlayer(player);
        }
    }

    @Override
    public void run() {
        // This is inefficient obviously, but I can't bother with every corner case personally
        for (Player player : disguises.keySet()) {
            Disguise disguise = disguises.get(player);
            disguise.update();
        }
    }

    public static void addDisguise(Player player, Disguise disguise) {
        removeDisguise(player);
        disguises.put(player, disguise);
        disguise.spawnLiving();
    }

    public static void removeDisguise(Player player) {
        Disguise disguise = disguises.get(player);
        if (disguise == null) {
            return;
        }
        disguise.deleteLiving();
        disguises.remove(player);
    }

    public static void showDisguise(Player player, Player disguised) {
        Disguise disguise = disguises.get(disguised);
        if (disguise == null) {
            return;
        }
        disguise.showDisguise(player);
    }

    public static void hideDisguise(Player player, Player disguised) {
        Disguise disguise = disguises.get(disguised);
        if (disguise == null) {
            return;
        }
        disguise.hideDisguise(player);
    }

    public static void showDisguises(Player player) {
        for (Player disguised : player.getWorld().getPlayers()) {
            if (disguised.equals(player)) {
                continue;
            }
            showDisguise(player, disguised);
        }
    }

    public static void initializePlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
                // Intercept player attack packet
                // If it's the entity the player is disguised as then redirect to the player instead
                // If we're attacking the player directly, we shouldn't be able to so cancel it
                if (msg instanceof PacketPlayInUseEntity) {
                    PacketPlayInUseEntity packet = (PacketPlayInUseEntity) msg;
                    Field f = packet.getClass().getDeclaredField("a");
                    f.setAccessible(true);
                    int id = Integer.parseInt(f.get(packet).toString());
                    PacketPlayInUseEntity.EnumEntityUseAction action = packet.a();
                    if (action.equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {
                        for (Disguise disguise : DisguiseManager.disguises.values()) {
                            if(disguise.getLiving() == null) {
                                continue;
                            }
                            // Ignore player melees in case they bounced the destroy entity packet
                            // This matters for disguises where the player is larger than the disguise
                            if(disguise.getOwner().getEntityId() == id) {
                                return;
                            }
                            if (disguise.getLiving().getId() == id ||
                                    disguise.getSquid().getId() == id) {
                                f.setInt(packet, disguise.getOwner().getEntityId());
                            }
                        }
                        for(Entity redirect_from : redirect_melee.keySet()) {
                            if(redirect_from == null || redirect_melee.get(redirect_from) == null) {
                                continue;
                            }
                            if(redirect_from.getEntityId() == id) {
                                f.setInt(packet, redirect_melee.get(redirect_from).getEntityId());
                            }
                        }
                    }
                }
                if (msg instanceof PacketPlayInArmAnimation) {
                    PacketPlayInArmAnimation packet = (PacketPlayInArmAnimation) msg;
                    // Make their disguise show the arm animation as well
                    for(Disguise disguise : DisguiseManager.disguises.values()) {
                        if(disguise.getLiving() == null) {
                            return;
                        }
                        if(!disguise.getOwner().getName().equals(channelHandlerContext.name())) {
                            continue;
                        }
                        if (disguise.getShowAttackAnimation()) {
                            PacketPlayOutAnimation arm_swing_packet = new PacketPlayOutAnimation(
                                    disguise.getLiving(), (byte) 0);
                            Utils.sendPacketToAll(arm_swing_packet);
                        }
                    }
                }
                super.channelRead(channelHandlerContext, msg);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object msg, ChannelPromise channelPromise) throws Exception {
                super.write(channelHandlerContext, msg, channelPromise);
            }
        };
        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        if (pipeline.get(player.getName()) != null) {
            pipeline.remove(player.getName());
        }
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        showDisguises(player);
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent e) {
        // This might be laggy and there's probably a better way to do it, hack fix
        Player player = e.getPlayer();
        for(Player other : player.getWorld().getPlayers()) {
            if(player.equals(other)) {
                continue;
            }
            double from_distance = other.getLocation().distance(e.getFrom());
            double to_distance = other.getLocation().distance(e.getTo());
            double despawn_distance = 80;
            if(from_distance > despawn_distance && to_distance <= despawn_distance) {
                showDisguise(player, other);
                showDisguise(other, player);
            }
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent e) {
        if(!e.getFrom().getWorld().equals(e.getTo().getWorld())) {
            return;
        }
        // This might be laggy and there's probably a better way to do it, hack fix
        Player player = e.getPlayer();
        for(Player other : player.getWorld().getPlayers()) {
            if(player.equals(other)) {
                continue;
            }
            double from_distance = other.getLocation().distance(e.getFrom());
            double to_distance = other.getLocation().distance(e.getTo());
            // Players seem to come into view at a distance of 45 blocks (on one axis)
            // And go out of view at a distance of 50 blocks (on one axis)
            double despawn_distance = 45;
            if(from_distance > despawn_distance && to_distance <= despawn_distance) {
                showDisguise(player, other);
                showDisguise(other, player);
            }
        }
    }

    @EventHandler
    public void playerChangedWorld(PlayerChangedWorldEvent e) {
        // Reload their disguises, and show their own disguise to other players
        showDisguises(e.getPlayer());
        for(Player player : e.getFrom().getPlayers()) {
            hideDisguise(player, e.getPlayer());
        }
        for(Player player : e.getPlayer().getWorld().getPlayers()) {
            showDisguise(player, e.getPlayer());
        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        initializePlayer(e.getPlayer());
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent e) {
        removeDisguise(e.getPlayer());
    }

}
