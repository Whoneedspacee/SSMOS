package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitIronGolem {
    public static void IronGolem(Player player){
        player.getInventory().clear();
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.DIAMOND_BOOTS);
        armor[1] = new ItemStack(Material.IRON_LEGGINGS);
        armor[2] = new ItemStack(Material.IRON_CHESTPLATE);
        armor[3] = new ItemStack(Material.IRON_HELMET);
        ItemMeta metaHelmet = armor[3].getItemMeta();
        ItemMeta metaChestplate = armor[2].getItemMeta();
        ItemMeta metaLeggings = armor[1].getItemMeta();
        ItemMeta metaBoots = armor[0].getItemMeta();
        metaHelmet.setUnbreakable(true);
        metaChestplate.setUnbreakable(true);
        metaLeggings.setUnbreakable(true);
        metaBoots.setUnbreakable(true);
        armor[3].setItemMeta(metaHelmet);
        armor[2].setItemMeta(metaChestplate);
        armor[1].setItemMeta(metaLeggings);
        armor[0].setItemMeta(metaBoots);

        player.getInventory().setArmorContents(armor);


        ItemStack Axe = new ItemStack(Material.IRON_AXE);
        ItemMeta metaAxe = Axe.getItemMeta();
        metaAxe.setDisplayName("Fissure");
        metaAxe.setUnbreakable(true);
        Axe.setItemMeta(metaAxe);
        player.getInventory().addItem(Axe);


        ItemStack Pickaxe = new ItemStack(Material.IRON_PICKAXE);
        ItemMeta metaPickaxe = Pickaxe.getItemMeta();
        metaPickaxe.setDisplayName("Iron Hook");
        metaPickaxe.setUnbreakable(true);
        Pickaxe.setItemMeta(metaPickaxe);
        player.getInventory().addItem(Pickaxe);


        ItemStack Shovel = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta metaShovel = Shovel.getItemMeta();
        metaShovel.setDisplayName("Seismic Slam");
        metaShovel.setUnbreakable(true);
        Shovel.setItemMeta(metaShovel);
        player.getInventory().addItem(Shovel);

    }
}
