package xyz.whoneedspacee.ssmos.commands;

import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpectate implements CommandExecutor {

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
        server.toggleSpectator(player);
        if (server.isSpectator(player)) {
            Utils.sendServerMessageToPlayer("You are now a Spectator!", player, ServerMessageType.GAME);
            return true;
        }
        Utils.sendServerMessageToPlayer("You are no longer a Spectator!", player, ServerMessageType.GAME);
        return true;
    }

}
