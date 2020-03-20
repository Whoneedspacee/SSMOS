package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitIronGolem extends Kit
{
	
	public KitIronGolem()
	{
		super();
		
		this.name = "IronGolem";
		
		this.armor = {
			Material.DIAMOND_BOOTS,
			Material.IRON_LEGGINGS,
			Material.IRON_CHESTPLATE,
			Material.IRON_HELMET
		};
		
		this.weapons = {
			Material.IRON_AXE,
			Material.IRON_PICKAXE,
			Material.IRON_SHOVEL
		};
		
		this.abilities = {
			new Fissure(),
			new IronHook(),
			new SeismicSlam()
		};
	}

}
