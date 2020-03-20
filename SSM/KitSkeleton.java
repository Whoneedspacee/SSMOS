package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSkeleton extends Kit
{
	
	public KitSkeleton()
	{
		super();
		
		this.name = "Skeleton";
		
		this.armor = {
			Material.CHAINMAIL_BOOTS,
			Material.CHAINMAIL_LEGGINGS,
			Material.CHAINMAIL_CHESTPLATE,
			Material.CHAINMAIL_HELMET
		};
		
		this.weapons = {
			Material.IRON_AXE,
			Material.BOW
		};
		
		this.abilities = {
			new BoneExplosion(),
			new RopedArrow()
		};
	}

}
