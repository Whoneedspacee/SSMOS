package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSlime extends Kit
{
	
	public KitSlime()
	{
		super();
		
		this.name = "Slime";
		
		this.armor = {
			Material.CHAINMAIL_BOOTS,
			Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_HELMET
		};
		
		this.weapons = {
			Material.IRON_SWORD,
			Material.IRON_AXE
		};
		
		this.abilities = {
			new SlimeRocket(),
			new SlimeSlam()
		};
	}

}
