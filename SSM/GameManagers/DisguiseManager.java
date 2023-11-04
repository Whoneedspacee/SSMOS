package SSM.GameManagers;

import SSM.GameManagers.Disguises.Disguise;
import SSM.SSM;
import SSM.Utilities.Utils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class DisguiseManager implements Listener, Runnable {

    private static DisguiseManager ourInstance;
    public static HashMap<Player, Disguise> disguises = new HashMap<Player, Disguise>();
    private JavaPlugin plugin = SSM.getInstance();

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
                    }
                }
                // Intercept player arm animation packet
                // For some reason when within 3-4 blocks of attacking an entity the game sends no packets
                // So we need to track this one to make abilities like roped arrow work
                /*if(msg instanceof PacketPlayInArmAnimation) {
                    Player player = Bukkit.getPlayer(channelHandlerContext.name());
                    Block block = player.getTargetBlock((HashSet<Byte>) null, 5);
                    new PlayerInteractEvent(player, Action.LEFT_CLICK_AIR,
                            player.getItemInHand(), block, null).callEvent();
                }*/
                if (msg instanceof PacketPlayInArmAnimation) {
                    PacketPlayInArmAnimation packet = (PacketPlayInArmAnimation) msg;
                    // Make their disguise show the arm animation as well
                    for(Disguise disguise : DisguiseManager.disguises.values()) {
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
                if (msg instanceof PacketPlayOutEntityMetadata) {
                    try {
                        PacketPlayOutEntityMetadata packet = (PacketPlayOutEntityMetadata) msg;
                        Field f = packet.getClass().getDeclaredField("a");
                        f.setAccessible(true);
                        int id = Integer.parseInt(f.get(packet).toString());
                        for (Player player : disguises.keySet()) {
                            // We found the player attached to the packet ID
                            // We also are sending this packet to the player
                            if (id == player.getEntityId() && player.getName().equals(channelHandlerContext.name())) {
                                //Bukkit.broadcastMessage(player.getName() + " " + id + " " + msg.toString());
                                // Send the same metadata but cancel out invisibility for ourselves
                                //DataWatcher dw = ((CraftLivingEntity) player).getHandle().getDataWatcher();
                                f = packet.getClass().getDeclaredField("b");
                                f.setAccessible(true);
                                List<DataWatcher.WatchableObject> objects = (List<DataWatcher.WatchableObject>) f.get(packet);
                                DataWatcher.WatchableObject invisible_data = null;
                                for (DataWatcher.WatchableObject check : objects) {
                                    //Bukkit.broadcastMessage(check.c() + " " + check.a() + " " + check.b().toString());
                                    int index = check.a();
                                    if (index == 0) {
                                        invisible_data = check;
                                        break;
                                    }
                                }
                                if (invisible_data == null) {
                                    break;
                                }
                                if ((((byte) invisible_data.b()) & 0x20) > 0) {
                                    // Must use new datawatcher since calling watch on the main one
                                    // Will make a new packet and cause a infinite chain
                                    DataWatcher dw = new DataWatcher(((CraftPlayer) player).getHandle());
                                    // A is the new index method so we'll use that for our fake datawatcher
                                    dw.a(0, (byte) ((byte) invisible_data.b() & (~0x20)));
                                    PacketPlayOutEntityMetadata no_invisibility_packet = new PacketPlayOutEntityMetadata(player.getEntityId(), dw, true);
                                    Utils.sendPacket(player, no_invisibility_packet);
                                    return;
                                    //if(objects.size() == 1) {
                                    //    return;
                                    //}
                                    // Remove this packet data, but still send the normal packet stuff
                                    //objects.remove(invisible_data);
                                    //f.set(packet, objects);
                                }
                                //invisible_data.a((byte) ((byte) invisible_data.b() & (~0x20)));
                                //Bukkit.broadcastMessage("Set: " + invisible_data.b());
                                //Bukkit.broadcastMessage("Player: " + player.getName() + " Id: " + player.getEntityId());
                                // We're sending to the disguised player so cancel invisibility
                                //Bukkit.broadcastMessage("Current Mask: " + mask.b());
                                //if (player.getName().equals(channelHandlerContext.name())) {
                                //mask.a((byte) ((byte) mask.b() & (~0x20)));
                                //Bukkit.broadcastMessage("Cancelled Invisibility of " + id + " for: " + player.getName());
                                //return;
                                //}
                                // We're sending to another player so add invisibility
                                //else {
                                //Bukkit.broadcastMessage("Added Invisibility of " + id + " for: " + channelHandlerContext.name());
                                //mask.a((byte) ((byte) mask.b() | 0x20));
                                //}
                                //Bukkit.broadcastMessage("After Mask: " + mask.b());
                                //f.set(packet, objects);
                                //Bukkit.broadcastMessage(mask.b().toString());
                                //dw.watch(0, (byte) ((byte) mask.b() & (~0x20)));
                                //PacketPlayOutEntityMetadata invisiblity_packet = new PacketPlayOutEntityMetadata(player.getEntityId(), dw, true);
                                //Utils.sendPacket();
                                //msg = invisiblity_packet;
                            }
                        }
                    } catch (Exception e) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Entity Metadata Packet Exception");
                    }
                }
                /*if(msg instanceof PacketPlayOutEntityEffect) {
                    PacketPlayOutEntityEffect packet = (PacketPlayOutEntityEffect) msg;
                    Field f = packet.getClass().getDeclaredField("a");
                    f.setAccessible(true);
                    int id = Integer.parseInt(f.get(packet).toString());
                    for(Player player : disguises.keySet()) {
                        // This is our disguised players id, and we're sending to that disguised player
                        if(id == player.getEntityId() && player.getName().equals(channelHandlerContext.name())) {
                            // Cancel this packet since we don't want ourself to be invisible
                            Bukkit.broadcastMessage("Cancelled invis: " + player.getName() + " " + player.getEntityId());
                            return;
                        }
                    }
                }*/
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
            double despawn_distance = 90;
            if(from_distance > despawn_distance && to_distance <= despawn_distance) {
                showDisguise(player, other);
                showDisguise(other, player);
            }
        }
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent e) {
        showDisguises(e.getPlayer());
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

    @EventHandler
    public void playerChangedWorld(PlayerChangedWorldEvent e) {
        // Reload their disguise, and show their own disguise to other players
        showDisguises(e.getPlayer());
        for(Player player : e.getPlayer().getWorld().getPlayers()) {
            showDisguise(player, e.getPlayer());
        }
    }

}
