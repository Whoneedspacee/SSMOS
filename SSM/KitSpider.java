package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSpider {
    public static void Spider(Player player){
        player.getInventory().clear();
        ItemStack[] armor = new ItemStack[3];
        armor[0] = new ItemStack(Material.IRON_BOOTS);
        armor[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        armor[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta metaChestplate = armor[2].getItemMeta();
        ItemMeta metaLeggings = armor[1].getItemMeta();
        ItemMeta metaBoots = armor[0].getItemMeta();
        metaChestplate.setUnbreakable(true);
        metaLeggings.setUnbreakable(true);
        metaBoots.setUnbreakable(true);
        armor[2].setItemMeta(metaChestplate);
        armor[1].setItemMeta(metaLeggings);
        armor[0].setItemMeta(metaBoots);

        player.getInventory().setArmorContents(armor);


        ItemStack Sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta metaSword = Sword.getItemMeta();
        metaSword.setDisplayName("Needler");
        metaSword.setUnbreakable(true);
        Sword.setItemMeta(metaSword);
        player.getInventory().addItem(Sword);


        ItemStack Axe = new ItemStack(Material.IRON_AXE);
        ItemMeta metaAxe = Axe.getItemMeta();
        metaAxe.setDisplayName("Spin Web");
        metaAxe.setUnbreakable(true);
        Axe.setItemMeta(metaAxe);
        player.getInventory().addItem(Axe);


}}
