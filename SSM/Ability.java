package me.SirInHueman.SSM;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_15_R1.ItemStack;

public class Ability
{
    
	String name = "Base";
	double cooldownTime = 2.5;
	boolean leftClickActivate = false;
	boolean rightClickActivate = false;
	
	public void activateLeft(Player player)
	{
		if(leftClickActivate)
		{
			checkAndActivate(player);
		}
	}
	
	public void activateRight(Player player)
	{
		if(rightClickActivate)
		{
			checkAndActivate(player);
		}
	}
	
	public void checkAndActivate(Player player)
	{
		ItemStack item = player.getInventory().getItemInMainHand();
		Material itemType = item.getType();
		if(player.getCooldown(itemType) > 0)
		{
			return;
		}
		player.setCooldown(itemType, cooldownTime * 20);
		useAbility(player);
    }
	
	public void useAbility(Player player)
	{
		World world = player.getWorld();
		world.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 10, 1);
		player.setVelocity(new Vector(0, 10, 0));
    }
	
}
