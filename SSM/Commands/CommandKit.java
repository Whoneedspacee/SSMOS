package SSM.Commands;

import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
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
        if (args.length >= 1) {
            StringBuilder builder = new StringBuilder();
            for (String value : args) {
                builder.append(value);
                builder.append(" ");
            }
            builder.deleteCharAt(builder.length() - 1);
            String kitname = builder.toString();
            for (Kit kit : KitManager.getAllKits()) {
                if (kit.getName().equalsIgnoreCase(kitname)) {
                    KitManager.equipPlayer(player, kit);
                    return true;
                }
            }
            return false;
        }

        int slot = 10; // beginning slot
        int count = 0;
        Inventory selectKit = Bukkit.createInventory(player, 45, "Choose a Kit");
        for (Kit kit : KitManager.getAllKits()) {
            ItemStack item = kit.getMenuItemStack();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + kit.getName().replace("_", " "));
            item.setItemMeta(itemMeta);

            selectKit.setItem(slot, item);
            slot++;
            count++;

            if (count % 7 == 0) {
                slot += 2;
            }
        }
//        player.getWorld().playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
        player.openInventory(selectKit);

        return true;
    }
}
