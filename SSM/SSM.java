package SSM;

import SSM.Abilities.Firefly;
import SSM.Abilities.SelectKit;
import SSM.Commands.*;
import SSM.GameManagers.CooldownManager;
import SSM.Kits.*;
import net.minecraft.server.v1_15_R1.DamageSource;
import net.minecraft.server.v1_15_R1.LightEngineLayerEventListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class SSM extends JavaPlugin implements Listener {

    int i = 0;

    public static HashMap<UUID, Kit> playerKit = new HashMap<UUID, Kit>();
    public static Kit[] allKits;
    public static CustomCommand[] allCommands;
    public static Plugin ourInstance;

    public static void main(String[] args) {
        // for testing junk
    }

    public static Plugin getInstance() {
        return ourInstance;
    }

    @Override
    public void onEnable() {
        ourInstance = this;
        getServer().getPluginManager().registerEvents(new CustomPvP(),this);
        getServer().getPluginManager().registerEvents(new SelectKit.ClickEvent(), this);
        getServer().getPluginManager().registerEvents(new Firefly(), this);
        getServer().getPluginManager().registerEvents(this, this);

        allKits = new Kit[]{
                //Put in order of how kits appear (It affects ordering).
                new KitSkeleton(),
                new KitIronGolem(),
                new KitSpider(),
                new KitSlime(),
                new KitSquid(),
                new KitCreeper(),
                new KitSnowMan(),
                new KitMagmaCube(),
                new KitWitch(),
                new KitBlaze(),
                new KitShulker(),
                new KitChoose(),
        };
        allCommands = new CustomCommand[]{
                new kit(),
                new damage(),
                new rank(),
        };

        CooldownManager.getInstance().start(this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player) sender;
        for (CustomCommand command : allCommands) {
            if (cmd.getName().equalsIgnoreCase(command.name)) {
                command.activate(p, args);
                return true;
            }
        }
        return false;
    }

    public static void equipPlayer(Player player, Kit check) {
        Kit kit = playerKit.get(player.getUniqueId());
        if (kit != null) {
            kit.destroyKit();
        }
        try {
            kit = check.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (kit != null) {
            kit.equipKit(player);
        }
        playerKit.put(player.getUniqueId(), kit);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockOn = e.getFrom().getBlock().getRelative(BlockFace.DOWN);
        if (blockOn.getType() == Material.GOLD_BLOCK) {
            Vector direction = player.getLocation().getDirection().setY(0);
            direction = direction.normalize().multiply(1.2);
            direction.setY(1.2);
            player.setVelocity(direction);
        }
        if (blockOn.getType() == Material.IRON_BLOCK) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(10);
            loc.add(dir);
            player.teleport(loc);
        }
        if (blockIn.isLiquid()) {
            player.setHealth(0.0);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String msg = event.getMessage();
        event.setFormat(event.getPlayer().getDisplayName() + " " + msg);
        msg = msg.replace(":b:", "" + ChatColor.DARK_RED + ChatColor.BOLD + "B" + ChatColor.RESET);
        event.setMessage(msg);
    }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name + " has fucking rage quit, what a fucking bitch LOL");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.getName().equals("Tap_That_Trap") || player.getName().equalsIgnoreCase("SSMGod") || player.getName().equalsIgnoreCase("Whoneedspacee")) {
            player.setDisplayName("" + ChatColor.MAGIC + ChatColor.BOLD + "N" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Developer" + ChatColor.RESET + ChatColor.MAGIC + ChatColor.BOLD + "N" + ChatColor.RESET + ChatColor.DARK_RED + " " + player.getName() + ChatColor.RESET);
        }
        if (player.getName().equalsIgnoreCase("HDSbjIhdihdgh2sf")) {
            player.setDisplayName("" + ChatColor.BLUE + ChatColor.BOLD + "Mag" + ChatColor.RESET + ChatColor.GOLD + " " + player.getName() + ChatColor.RESET);
        }
    }

    @EventHandler
    public void whenHit(EntityDamageEvent e) {
            Player p = (Player) e.getEntity();
            p.getServer().broadcastMessage("" + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + e.getCause());
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                e.setDamage(1000);
            }
        }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player player = e.getEntity();
        e.setDeathMessage("" + ChatColor.BLUE + "Death> " + ChatColor.YELLOW + player.getName() + " " + ChatColor.GRAY + "killed by " + ChatColor.GREEN + "Gaming addiction");
    }
}