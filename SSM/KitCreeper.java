package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreeper {
    public static void Creeper(Player player){
        player.getInventory().clear();
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.IRON_BOOTS);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[3] = new ItemStack(Material.LEATHER_HELMET);
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
        metaAxe.setDisplayName("Sulphur Bomb");
        metaAxe.setUnbreakable(true);
        Axe.setItemMeta(metaAxe);
        player.getInventory().addItem(Axe);


        ItemStack Shovel = new ItemStack(Material.IRON_SHOVEL);
        ItemMeta metaShovel = Shovel.getItemMeta();
        metaShovel.setDisplayName("Explode");
        metaShovel.setUnbreakable(true);
        Shovel.setItemMeta(metaShovel);
        player.getInventory().addItem(Shovel);
    }
}
