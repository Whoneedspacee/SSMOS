package ssm.commands;

import ssm.gamemanagers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGetState implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        Bukkit.broadcastMessage(GameManager.GameState.toString(GameManager.getState()));
        return true;
    }

}
