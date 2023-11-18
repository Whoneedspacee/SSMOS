package ssm.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.managers.GameManager;

public class CommandServer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        GameManager.openServerMenu(player);
        return true;
    }

}
