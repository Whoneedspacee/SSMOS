package SSM;

import SSM.Abilities.*;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.MeleeManager;
import SSM.Kits.*;
import SSM.Utilities.DamageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import java.util.List;
import java.util.UUID;

public class SSM extends JavaPlugin implements Listener {

    public static HashMap<UUID, Kit> playerKit = new HashMap<UUID, Kit>();
    public static Kit[] allKits;
    public static Ability[] heroAbilities;
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

        allKits = new Kit[]{
            //Put in order of how kits appear (It affects ordering).
            new KitSkeleton(),
            new KitIronGolem(),
            new KitSpider(),
            new KitSlime(),
            new KitSquid(),
            new KitCreeper(),
            new KitSnowMan(),
            new KitWolf(),
            new KitMagmaCube(),
            new KitWitch(),
            new KitCow(),
            new KitPig(),
            new KitChoose(),
        };

        CooldownManager.getInstance().start(this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            if (args.length == 1) {
                for (Kit check : allKits) {
                    if (check.name.equalsIgnoreCase(args[0])) {
                        equipPlayer(player, check);
                        return true;
                    }
                }
            }
            String finalMessage = "Kit Choices: ";
            for (Kit kit : allKits) {
                finalMessage += kit.getName() + " ";
            }
            player.sendMessage(finalMessage);
    }else if (cmd.getName().equalsIgnoreCase("damage")) {
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
            direction.setY(1.2);
            player.setVelocity(direction);
        }
        if (blockOn.getType() == Material.IRON_BLOCK) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection();
            dir.multiply(10);
            loc.add(dir);
            player.teleport(loc);
        }
        if (blockIn.isLiquid()) {
            player.setHealth(0.0);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String msg = event.getMessage();
        event.setFormat(event.getPlayer().getDisplayName()+" "+msg);
        msg = msg.replace(":b:", ""+ChatColor.DARK_RED + ChatColor.BOLD + "B" +ChatColor.RESET);
        event.setMessage(msg);
        }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        e.setCancelled(false);
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(false);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID){
            LivingEntity ent = (LivingEntity)e.getEntity();
            ent.setHealth(-1.0);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name + " has fucking rage quit, what a fucking bitch LOL");
    }
}
