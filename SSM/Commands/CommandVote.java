package SSM.Commands;

import SSM.GameManagers.GameManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.Kit;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;

public class CommandVote implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        openVotingMenu(player);
        return true;
    }

    public static void openVotingMenu(Player player) {
        if(GameManager.getState() > GameManager.GameState.LOBBY_VOTING) {
            player.sendMessage("You may not vote after the voting period has ended.");
            return;
        }
        Inventory selectMap = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Vote for a Map");
        for (MapFile mapfile : GameManager.all_maps) {
            ItemStack item = new ItemStack(Material.PAPER, GameManager.getVotesFor(mapfile) + 1);
            if(mapfile.equals(GameManager.getCurrentVotedMap(player))) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 20);
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Lists.newArrayList(mapfile.getName()));
            itemMeta.setDisplayName(ChatColor.RESET + mapfile.getName());
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
            selectMap.addItem(item);
        }
        player.openInventory(selectMap);
    }

}
