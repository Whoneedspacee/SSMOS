package SSM.Abilities;

import SSM.Ability;
import SSM.Kit;
import SSM.SSM;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class SelectKit extends Ability {

    private static Inventory selectKit;

    public SelectKit(){
        super();
        this.name = "" + ChatColor.BOLD + ChatColor.BLUE + "Select Kit";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
    }

    public void activate(){
        selectKit = Bukkit.createInventory(owner, 54,  ChatColor.BLUE + "Select Your Kit");
        for (Kit kit : SSM.allKits){
            ItemStack item = kit.menuItem;
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + kit.getName().replace("_", " "));
            item.setItemMeta(itemMeta);
            selectKit.addItem(item);
            owner.openInventory(selectKit);
        }
    }

    public static class ClickEvent implements Listener{

        @EventHandler
    public void clickEvent(InventoryClickEvent e){
            Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory().getSize()==54) {
            for (Kit kit : SSM.allKits){
                if (e.getCurrentItem().getType() == kit.menuItem.getType()){
                    kit.equipKit(player);
                    player.closeInventory();
                }
            }
            e.setCancelled(true);
        }


    }
}
}
