package xyz.whoneedspacee.ssmos.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return true;
        }
        if (server.getState() <= GameState.GAME_STARTING) {
            server.setTimeLeft(0);
        }
        return true;
    }

}
