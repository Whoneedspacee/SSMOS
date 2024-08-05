package xyz.whoneedspacee.ssmos.commands;

import xyz.whoneedspacee.ssmos.managers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;

public class CommandVote implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return true;
        }
        server.openVotingMenu(player);
        return true;
    }

}
