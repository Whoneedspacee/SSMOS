package ssm.commands;

import ssm.gamemanagers.GameManager;
import ssm.gamemanagers.maps.MapFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
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
        if (!canVote()) {
            player.sendMessage("You may not vote after the voting period has ended.");
            return;
        }
        int size = GameManager.getCurrentGamemode().getAllowedMaps().size() / 7 + 2;
        Inventory selectMap = Bukkit.createInventory(player, 9 * size  , "Choose a Map");

        List<MapFile> sortedMaps = new ArrayList<>(GameManager.getCurrentGamemode().getAllowedMaps());
        sortedMaps.sort(Comparator.comparing(MapFile::getName));

        int slot = 10;
        int count = 0;
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
            itemMeta.setDisplayName(ChatColor.YELLOW + mapfile.getName());
            item.setItemMeta(itemMeta);

            selectMap.setItem(slot, item);
            slot++;
            count++;

            if (count % 7 == 0) {
                slot += 2;
            }

            ItemStack exit = new ItemStack(Material.BARRIER);
            ItemMeta exitMeta = exit.getItemMeta();
            exitMeta.setDisplayName(ChatColor.RED + "Exit");
            exit.setItemMeta(exitMeta);
            selectMap.setItem(4, exit);
        }
        player.openInventory(selectMap);
    }

    public static boolean canVote() {
        if (GameManager.getState() >= GameManager.GameState.LOBBY_STARTING &&
                GameManager.getState() <= GameManager.GameState.GAME_STARTING) {
            return false;
        }
        return true;
    }

}
