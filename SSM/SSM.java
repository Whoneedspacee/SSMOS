package SSM;

import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.EventManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.MeleeManager;
import SSM.Utilities.DamageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SSM extends JavaPlugin implements Listener {

    private static JavaPlugin ourInstance;

    public static JavaPlugin getInstance() {
        return ourInstance;
    }

    @Override
    public void onEnable() {
        ourInstance = this;
        getServer().getPluginManager().registerEvents(this, this);
        new CooldownManager();
        new EventManager();
        new KitManager();
        new MeleeManager();
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("damage")) {
            if (args.length == 1) {
                try {
                    int number = Integer.parseInt(args[0]);
                    DamageUtil.dealDamage(player, number);
                    player.sendMessage("You were dealt " + number + " damage");
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("You need to input a number!");
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("setspeed")) {
            if (args.length == 1) {
                try {
                    float number = Float.parseFloat(args[0]);
                    player.setWalkSpeed(number);
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("You need to input a number!");
                    return true;
                }
            }
        }
        // Should be 8000 in the velocity packet
        if (cmd.getName().equalsIgnoreCase("jump")) {
            CraftPlayer craftplayer = (CraftPlayer) player;
            float power = 1;
            if(args.length == 1) {
                power = Float.parseFloat(args[0]);
            }
            craftplayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), 0, power, 0));
            return true;
        }
        // Should be 8000 in the velocity packet
        if (cmd.getName().equalsIgnoreCase("move")) {
            CraftPlayer craftplayer = (CraftPlayer) player;
            float power = 1;
            if(args.length == 1) {
                power = Float.parseFloat(args[0]);
            }
            craftplayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), power, 0, 0));
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockOn = e.getFrom().getBlock().getRelative(BlockFace.DOWN);
        if (blockIn.isLiquid() && !player.isDead()) {
            player.setHealth(0.0);
        }
    }

    /*@EventHandler
    public void onPlayerJoin(PlayerMoveEvent e) {
        Location temp = e.getTo().clone().subtract(e.getFrom());
        String print = String.format("%.5f, %.5f, %.5f", temp.getX(), temp.getY(), temp.getZ());
        e.getPlayer().sendMessage(print);
    }*/

    /*@EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        injectPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
    }

    public void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                //if the server is sending a packet, the function "write" will be called. If you want to cancel a specific packet, just use return; Please keep in mind that using the return thing can break the intire server when using the return thing without knowing what you are doing.
                super.write(channelHandlerContext, packet, channelPromise);
                if(packet instanceof PacketPlayOutEntityVelocity) {
                    PacketPlayOutEntityVelocity velocity_packet = (PacketPlayOutEntityVelocity) packet;
                    PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer());
                    velocity_packet.b(data);
                    //player.sendMessage(data.readInt() + " " + player.getEntityId());
                    //if(player.getEntityId() != data.readInt()) {
                    //    player.sendMessage(data.readShort() + ", " + data.readShort() + ", " + data.readShort());
                    //}
                }
            }


        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);

    }

    public void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }*/

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            LivingEntity ent = (LivingEntity) e.getEntity();
            ent.setHealth(0.0);
        }
    }
}

