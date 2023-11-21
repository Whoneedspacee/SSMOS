package ssm.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

import java.util.HashMap;

public class CommandReply implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        Player last_received = CommandMessage.last_received_from.get(player);
        if(last_received == null) {
            Utils.sendServerMessageToPlayer("Could not find player to reply to.", player, ServerMessageType.COMMAND);
            return true;
        }
        String[] new_args = new String[args.length + 1];
        new_args[0] = last_received.getName();
        System.arraycopy(args, 0, new_args, 1, args.length);
        CommandMessage.sendMessage(commandSender, command, commandLabel, new_args);
        return true;
    }

}
