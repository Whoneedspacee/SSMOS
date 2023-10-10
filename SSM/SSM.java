package SSM;

import SSM.GameManagers.*;
import SSM.GameManagers.Disguise.Disguise;
import SSM.Kits.KitSkeleton;
import SSM.Utilities.DamageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
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
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
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
        new DamageManager();
        new DisguiseManager();
        // Do not do anything before manager creation please
        for(Player player : Bukkit.getOnlinePlayers()) {
            Bukkit.broadcastMessage("Equipped: " + player.getName());
            KitManager.equipPlayer(player, KitManager.getAllKits().get(0));
        }
    }

    @Override
    public void onDisable() {
        for(Disguise disguise : DisguiseManager.disguises.values()) {
            disguise.deleteLiving();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("damage")) {
            if (args.length == 1) {
                try {
                    int number = Integer.parseInt(args[0]);
                    DamageUtil.damage(player, null, number);
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
            DamageUtil.damage(player, null, 1000, 0, true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            LivingEntity ent = (LivingEntity) e.getEntity();
            //ent.getWorld().strikeLightningEffect(ent.getLocation());
            DamageUtil.damage(ent, null, 1000, 0, true);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String playerName = e.getPlayer().getName();
        String newMessage = ChatColor.YELLOW + playerName + ChatColor.WHITE + " " + message;
        if(e.getPlayer().isOp()) {
            newMessage = ChatColor.RED + playerName + ChatColor.WHITE + " " + message;
        }
        e.setFormat(newMessage);
    }

    @EventHandler
    public void onWeatherChangeEvent(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setFoodLevel(20);
    }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

}

