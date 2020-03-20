package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSpider extends Kit 
{
	
	public KitSpider()
	{
		super();
		
		this.name = "Spider";
		
		this.armor = {
			Material.IRON_BOOTS,
			Material.CHAINMAIL_LEGGINGS,
			Material.CHAINMAIL_CHESTPLATE,
		};
		
		this.weapons = {
			Material.IRON_SWORD,
			Material.IRON_AXE
		};
		
		this.abilities = {
			new Needler(),
			new SpinWeb()
		};
	}

}
