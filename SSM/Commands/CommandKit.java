package SSM.Commands;

import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandKit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        if(args.length >= 1) {
            StringBuilder builder = new StringBuilder();
            for (String value : args) {
                builder.append(value);
                builder.append(" ");
            }
            builder.deleteCharAt(builder.length() - 1);
            String kitname = builder.toString();
            for (Kit kit : KitManager.getAllKits()) {
                if(kit.getName().equalsIgnoreCase(kitname)) {
                    KitManager.equipPlayer(player, kit);
                    return true;
                }
            }
            return false;
        }
        Inventory selectKit = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Select Your Kit");
        for (Kit kit : KitManager.getAllKits()) {
            ItemStack item = kit.getMenuItemStack();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + kit.getName().replace("_", " "));
            item.setItemMeta(itemMeta);
            selectKit.addItem(item);
        }
        player.openInventory(selectKit);
        return true;
    }

}
