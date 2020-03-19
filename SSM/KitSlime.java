package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSlime {
    public static void Slime(Player player){
        player.getInventory().clear();
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.CHAINMAIL_BOOTS);
        armor[1] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        armor[2] = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemMeta metaHelmet = armor[2].getItemMeta();
        ItemMeta metaChestplate = armor[1].getItemMeta();
        ItemMeta metaBoots = armor[0].getItemMeta();
        metaHelmet.setUnbreakable(true);
        metaChestplate.setUnbreakable(true);
        metaBoots.setUnbreakable(true);
        armor[2].setItemMeta(metaHelmet);
        armor[1].setItemMeta(metaChestplate);
        armor[0].setItemMeta(metaBoots);

        player.getInventory().setHelmet(armor[2]);
        player.getInventory().setChestplate(armor[1]);
        player.getInventory().setLeggings(armor[0]);


        ItemStack Sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta metaSword = Sword.getItemMeta();
        metaSword.setDisplayName("Slime Rocket");
        metaSword.setUnbreakable(true);
        Sword.setItemMeta(metaSword);
        player.getInventory().addItem(Sword);


        ItemStack Axe = new ItemStack(Material.IRON_AXE);
        ItemMeta MetaAxe = Axe.getItemMeta();
        MetaAxe.setDisplayName("Slime Slam");
        MetaAxe.setUnbreakable(true);
        Axe.setItemMeta(MetaAxe);
        player.getInventory().addItem(Axe);
    }
}
