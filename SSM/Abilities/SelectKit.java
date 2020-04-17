package SSM.Abilities;

import SSM.Ability;
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

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class SelectKit extends Ability {

    public SelectKit(){
        super();
        this.name = "" + ChatColor.BOLD + ChatColor.BLUE + "Select Kit";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
    }
    public void activate(){
        Player player = (Player) owner;
        Inventory selectKit = Bukkit.createInventory(player, 54,  ChatColor.BLUE + "Select Your Kit");

        ItemStack skeleton = new ItemStack(Material.BOW);
        ItemStack iron_golem = new ItemStack(Material.IRON_BLOCK);
        ItemStack spider = new ItemStack(Material.SPIDER_EYE);
        ItemStack slime = new ItemStack(Material.SLIME_BALL);
        ItemStack squid = new ItemStack(Material.INK_SAC);
        ItemStack creeper = new ItemStack (Material.GUNPOWDER);

        ItemMeta skeleton_meta = skeleton.getItemMeta();
        skeleton_meta.setDisplayName(ChatColor.RESET + "Skeleton");
        skeleton.setItemMeta(skeleton_meta);

        ItemStack[] kits = {skeleton, iron_golem, spider , slime, squid, creeper};
        selectKit.setContents(kits);
        player.openInventory(selectKit);
    }
    public static class ClickEvent implements Listener{

        @EventHandler
    public void clickEvent(InventoryClickEvent e){

            Player player = (Player) e.getWhoClicked();

        if (e.getClickedInventory().getSize()==54) {
            switch (e.getCurrentItem().getType()) {
                case BOW:
                    player.closeInventory();
                    player.performCommand("kit skeleton");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Skeleton.");
                    break;
                case IRON_BLOCK:
                    player.closeInventory();
                    player.performCommand("kit iron_golem");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Iron Golem.");
                    break;
                case SPIDER_EYE:
                    player.closeInventory();
                    player.performCommand("kit spider");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Spider.");
                    break;
                case SLIME_BALL:
                    player.closeInventory();
                    player.performCommand("kit slime");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Slime.");
                    break;
                case INK_SAC:
                    player.closeInventory();
                    player.performCommand("kit squid");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Squid.");
                    break;
                case GUNPOWDER:
                    player.closeInventory();
                    player.performCommand("kit creeper");
                    player.sendMessage(""+ChatColor.RESET + ChatColor.BLUE + "You selected Creeper.");
                    break;
            }
            e.setCancelled(true);
        }


    }
}
}
