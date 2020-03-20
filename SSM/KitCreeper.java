package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreeper extends Kit 
{
    
	public KitCreeper()
	{
		super();
		
		this.name = "Creeper";
		
		this.armor = {
			Material.IRON_BOOTS,
			Material.LEATHER_LEGGINGS,
			Material.LEATHER_CHESTPLATE,
			Material.LEATHER_HELMET
		};
		
		this.weapons = {
			Material.IRON_AXE,
			Material.IRON_SHOVEL
		};
		
		this.abilities = {
			new SulphurBomb(),
			new Explode()
		};
	}
	
}
