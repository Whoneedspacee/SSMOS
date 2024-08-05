package xyz.whoneedspacee.ssmos.commands;

import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSetKit implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if (!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return true;
        }
        if (args.length >= 2) {
            Player set = Bukkit.getPlayer(args[0]);
            if (set == null) {
                player.sendMessage("Player not found!");
                return true;
            }
            StringBuilder kit_builder = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                kit_builder.append(args[i]);
                kit_builder.append(" ");
            }
            String kit_string = kit_builder.substring(0, kit_builder.length() - 1);
            Kit kit = null;
            for (Kit check : server.getCurrentGamemode().getAllowedKits()) {
                if (check.getName().equalsIgnoreCase(kit_string)) {
                    kit = check;
                    break;
                }
            }
            if (kit == null) {
                player.sendMessage("Kit not found!");
                return true;
            }
            KitManager.equipPlayer(set, kit);
            for (Player to_message : set.getWorld().getPlayers()) {
                Utils.sendServerMessageToPlayer(ChatColor.YELLOW + set.getName() + ChatColor.GRAY +
                        " had their kit set to " + ChatColor.GREEN + kit.getName() + ChatColor.GRAY + ".", to_message, ServerMessageType.GAME);
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        List<String> list = new ArrayList<String>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }

}
