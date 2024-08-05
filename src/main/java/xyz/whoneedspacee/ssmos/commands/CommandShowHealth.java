package xyz.whoneedspacee.ssmos.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;

public class CommandShowHealth implements CommandExecutor {

    public static boolean show_health = false;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        if (!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        if(show_health) {
            Utils.sendServerMessageToPlayer("Players can no longer see health.",
                    player, ServerMessageType.ADMIN);
            show_health = false;
            return true;
        }
        Utils.sendServerMessageToPlayer("Players can now see health.",
                player, ServerMessageType.ADMIN);
        show_health = true;
        return true;
    }

}
