package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit 
{
	
	// used for finding the kit to equip on command, ex: /kit name
	public String name;
	public double damage;
	public double knockback;
	public double regeneration;
	public double speed;
	
	// list of materials for armor
	protected Material[] armor;
	
	// list of materials for weapons
	protected Material[] weapons;
	
	// assigned to the weapons above by index, ex: 1st ability goes on the 1st weapon, etc
	public Ability[] abilities;
	
    public void equipKit(Player player)
    {
    	player.getInventory().clear();
        for(Material material : armor)
        {
        	ItemStack item = new ItemStack(material);
        	ItemMeta meta = item.getItemMeta();
        	meta.setUnbreakable(true);
        	item.setItemMeta(meta);
        }
        player.getInventory().setArmorContents(armor);
        for(int i = 0; i < weapons.length; i++) 
        {
        	Material material = weapons[i];
        	Ability ability = abilities[i];
        	ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ability.name);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        }
    }
    
}
