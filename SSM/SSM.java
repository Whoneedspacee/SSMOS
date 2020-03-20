package me.SirInHueman.SSM;

import org.bukkit.Bukkit;
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
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_15_R1.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SSM extends JavaPlugin implements Listener 
{

	HashMap<String, Kit> playerKit = new HashMap<String, Kit>();
	Kit[] allKits = {
		new KitCreeper(),
		new KitSpider()
	};
	
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable()
    {
    	
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
    {
        if (cmd.getName().equalsIgnoreCase("kit"))
        {
        	Player player = sender.getServer().getPlayer(args[0]);
            if(args.length == 1)
            {
            	for(Kit kit : allKits)
            	{
            		if(kit.name == args[0]) {
            			kit.equipKit(player);
            			playerKit.replace(player.getUniqueId(), kit);
            			return true;
            		}
            	}
            }
            String finalMessage = "Kit Choices";
    		for(Kit kit : allKits)
    		{
    			finalMessage += ", " + kit.name;
    		}
    		player.sendMessage(finalMessage);
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) 
    {
        Player player = e.getPlayer();
        Kit kit = playerKit.get(player.getUniqueId());
        int selected = player.getInventory().getHeldItemSlot();
        if(!kit || !selected)
        {
        	return;
        }
        if(selected > kit.abilities.length)
        {
        	return;
        }
        Ability using = kit.abilities[selected - 1];
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
        {
        	using.activateLeft();
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
        	using.activateRight();
        }

    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        Player player = e.getPlayer();
        Block blockOn = e.getTo().getBlock();
        if (blockOn.getType() == Material.GOLD_BLOCK){
            Double x = player.getLocation().getDirection().getX() * 1.2;
            Double z = player.getLocation().getDirection().getZ() * 1.2;
            player.setVelocity(new Vector(x, 1.2, z));
        }
        if (blockOn.isLiquid()){
            player.setHealth(0.0);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name + " has fucking rage quit, what a fucking bitch LOL");
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
        Player player = e.getPlayer();
        Entity NPC = e.getRightClicked();
        if (NPC.getCustomName().equalsIgnoreCase("Alchemist"))
        {
            int potion = (int) (Math.random() * 10) + 1;
            switch(potion)
            {
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












