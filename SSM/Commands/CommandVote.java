package SSM.Commands;

import SSM.GameManagers.GameManager;
import SSM.GameManagers.Maps.MapFile;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        if (GameManager.getState() > GameManager.GameState.LOBBY_VOTING) {
            player.sendMessage("You may not vote after the voting period has ended.");
            return;
        }
        Inventory selectMap = Bukkit.createInventory(player, 54, "Choose a Map");

        List<MapFile> sortedMaps = new ArrayList<>(GameManager.all_maps);
        sortedMaps.sort(Comparator.comparing(MapFile::getName));

        int slot = 0;
        for (MapFile mapfile : sortedMaps) {
            ItemStack item;
            if (GameManager.getVotesFor(mapfile) == 0) {
                item = new ItemStack(Material.PAPER);
            } else {
                item = new ItemStack(Material.EMPTY_MAP, GameManager.getVotesFor(mapfile));
            }
            if (mapfile.equals(GameManager.getCurrentVotedMap(player))) {
                item = new ItemStack(Material.MAP, GameManager.getVotesFor(mapfile));
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Lists.newArrayList(mapfile.getName()));
            itemMeta.setDisplayName(ChatColor.RESET + mapfile.getName());
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(itemMeta);
            selectMap.setItem(9 + slot, item);
            slot++;

            ItemStack bed = new ItemStack(Material.BED);
            ItemMeta bedMeta = bed.getItemMeta();
            bedMeta.setLore(Lists.newArrayList("lore"));
            bed.setItemMeta(bedMeta);
            selectMap.setItem(4, bed);
        }
        player.openInventory(selectMap);
    }
}
