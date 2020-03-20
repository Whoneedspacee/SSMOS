package SSM;

import SSM.Kits.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;


public class SSM extends JavaPlugin implements Listener {

    HashMap<UUID, Kit> playerKit = new HashMap<UUID, Kit>();
    Kit[] allKits = {
            new KitCreeper(),
            new KitIronGolem(),
            new KitSkeleton(),
            new KitSlime(),
            new KitSpider()
    };

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    public static void main(String[] args) {
        // for testing junk
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
                        Kit kit = null;
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
                        if (kit == null) {
                            return true;
                        }
                        kit.equipKit(player);
                        playerKit.put(player.getUniqueId(), kit);
                        return true;
                    }
                }
            }
            String finalMessage = "Kit Choices";
            for (Kit kit : allKits) {
                finalMessage += ", " + kit.name;
            }
            player.sendMessage(finalMessage);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Kit kit = playerKit.get(player.getUniqueId());
        int selected = player.getInventory().getHeldItemSlot();
        if (kit == null) {
            return;
        }
        if (selected >= kit.abilities.length) {
            return;
        }
        Ability using = kit.abilities[selected];
        if(using == null) {
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            using.activateLeft(player);
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            using.activateRight(player);
        }

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockOn = e.getTo().getBlock();
        if (blockOn.getType() == Material.GOLD_BLOCK) {
            Double x = player.getLocation().getDirection().getX() * 1.2;
            Double z = player.getLocation().getDirection().getZ() * 1.2;
            player.setVelocity(new Vector(x, 1.2, z));
        }
        if (blockOn.isLiquid()) {
            player.setHealth(0.0);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name + " has fucking rage quit, what a fucking bitch LOL");
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity NPC = e.getRightClicked();
        if (NPC.getCustomName().equalsIgnoreCase("Alchemist")) {
            int potion = (int) (Math.random() * 10) + 1;
            switch (potion) {
                case 1:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
                    break;
                case 2:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 2));
                    break;
                case 3:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2));
                    break;
                case 4:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2));
                    break;
                case 5:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20, 2));
                    break;
                case 6:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 2));
                    break;
                case 7:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 2));
                    break;
                case 8:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 2));
                    break;
                case 9:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 20));
                    break;
                case 10:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 20));
                    break;
            }
            NPC.remove();
        }
    }

}












