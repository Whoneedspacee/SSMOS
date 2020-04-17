package SSM;

import SSM.Abilities.SelectKit;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.DamageManager;
import SSM.Kits.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;

public class SSM extends JavaPlugin implements Listener {

    public static HashMap<UUID, Kit> playerKit = new HashMap<UUID, Kit>();
    public static Kit[] allKits;
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
        getServer().getPluginManager().registerEvents(new SelectKit.ClickEvent(), this);
        getServer().getPluginManager().registerEvents(this, this);

        allKits = new Kit[]{
            new KitCreeper(),
            new KitIronGolem(),
            new KitSkeleton(),
            new KitSlime(),
            new KitSpider(),
            new KitWitch(),
            new KitShulker(),
            new KitSquid(),
            new KitSnowMan(),
            new KitMagmaCube(),
            new KitChoose(),
        };

        CooldownManager.getInstance().start(this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
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
            return true;
        }else if (cmd.getName().equalsIgnoreCase("damage")){
            if (args.length == 1){
                Player player = (Player)sender;
                Kit kit = playerKit.get(player.getUniqueId());
                player.damage(DamageManager.finalDamage(Integer.parseInt(args[0]), kit.armor));
                player.sendMessage("You were dealt "+Integer.parseInt(args[0])+" damage");

            }
        }
        return false;
    }

    public void equipPlayer(Player player, Kit check) {
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
            dir.multiply(10); //5 blocks a way
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
        msg = msg.replace(":b:", ""+ChatColor.RED + ChatColor.BOLD + "B" +ChatColor.RESET);
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
    public void meleeDamage(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player)){
            return;
        }
        if (!(e.getEntity() instanceof Player)){
            Player damager = (Player)e.getDamager();
            LivingEntity target = (LivingEntity)e.getEntity();
            Kit damagerKit = playerKit.get(damager.getUniqueId());
            target.damage(DamageManager.finalDamage(damagerKit.damage, 0));
        }
        e.setCancelled(true);
        Player damager = (Player)e.getDamager();
        LivingEntity target = (LivingEntity)e.getEntity();
        Kit damagerKit = playerKit.get(damager.getUniqueId());
        Kit targetKit = playerKit.get(target.getUniqueId());
        target.damage(DamageManager.finalDamage(damagerKit.damage, targetKit.armor));
        Vector enemy = target.getLocation().toVector();
        Vector player = damager.getLocation().toVector();
        Vector pre = enemy.subtract(player);
        Vector velocity = pre.normalize().multiply(damagerKit.damage/2.5);
        target.setVelocity(new Vector(velocity.getX(), 0.45, velocity.getZ()));
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name + " has fucking rage quit, what a fucking bitch LOL");
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.performCommand("kit Selecting");
        if (player.getName().equals("huxs")||player.getName().equalsIgnoreCase("RyukoMatoiKLK")||player.getName().equalsIgnoreCase("Whoneedspacee")){
            player.setDisplayName(""+ChatColor.MAGIC + ChatColor.BOLD + "N"+ChatColor.DARK_PURPLE + ChatColor.BOLD + "Developer" +ChatColor.RESET +ChatColor.MAGIC + ChatColor.BOLD + "N" + ChatColor.RESET + ChatColor.DARK_RED + " " +player.getName() + ChatColor.RESET);
        }
        if (player.getName().equalsIgnoreCase("HDSbjIhdihdgh2sf")){
            player.setDisplayName("" + ChatColor.BLUE + ChatColor.BOLD + "Mag" + ChatColor.RESET + ChatColor.GOLD + " " + player.getName() + ChatColor.RESET);
        }
    }
    @EventHandler
    public void whenHit(EntityDamageEvent e){
        Entity entity = e.getEntity();
        entity.getServer().broadcastMessage(""+ChatColor.BOLD + ChatColor.LIGHT_PURPLE+e.getCause());
    }
}