package xyz.whoneedspacee.ssmos.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;

import java.util.HashMap;

public class CommandMessage implements CommandExecutor {

    public static HashMap<Player, Player> last_received_from = new HashMap<Player, Player>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        sendMessage(commandSender, command, commandLabel, args);
        return true;
    }

    public static void sendMessage(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        Player player = (Player) commandSender;
        if(args.length < 1) {
            Utils.sendServerMessageToPlayer("You must specify a player to message.", player, ServerMessageType.COMMAND);
            return;
        }
        Player receiver = null;
        // Check for exact names first
        for (Player check : Bukkit.getOnlinePlayers()) {
            if(receiver != null) {
                break;
            }
            if (check.getName().equalsIgnoreCase(args[0])) {
                receiver = check;
            }
        }
        // Check for matched names
        for (Player check : Bukkit.getOnlinePlayers()) {
            if(receiver != null) {
                break;
            }
            if (check.getName().toLowerCase().contains(args[0].toLowerCase())) {
                receiver = check;
            }
        }
        if(receiver == null) {
            Utils.sendServerMessageToPlayer("Could not find player specified.", player, ServerMessageType.COMMAND);
            return;
        }
        StringBuilder message_builder = new StringBuilder();
        message_builder.append(ChatColor.GOLD);
        message_builder.append(ChatColor.BOLD);
        message_builder.append(commandSender.getName());
        message_builder.append(" > ");
        message_builder.append(receiver.getName());
        message_builder.append(" ");
        message_builder.append(ChatColor.YELLOW);
        message_builder.append(ChatColor.BOLD);
        for (int i = 1; i < args.length; i++) {
            message_builder.append(args[i]);
            message_builder.append(" ");
        }
        if(args.length < 2) {
            message_builder.append("amogus ");
        }
        String private_message = message_builder.substring(0, message_builder.length() - 1);
        player.sendMessage(private_message);
        receiver.sendMessage(private_message);
        receiver.playSound(receiver.getLocation(), Sound.NOTE_PIANO, 1, 1.25f);
        Bukkit.getConsoleSender().sendMessage(private_message);
        last_received_from.put(receiver, player);
    }

}
