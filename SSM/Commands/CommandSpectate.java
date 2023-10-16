package SSM.Commands;

import SSM.GameManagers.GameManager;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpectate implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        if(GameManager.getState() > GameManager.GameState.LOBBY_STARTING) {
            Utils.sendServerMessageToPlayer("You cannot toggle Spectator during games.", player, ServerMessageType.GAME);
            return true;
        }
        GameManager.toggleSpectator(player);
        if(GameManager.isSpectator(player)) {
            Utils.sendServerMessageToPlayer("You are now a Spectator!", player, ServerMessageType.GAME);
            return true;
        }
        Utils.sendServerMessageToPlayer("You are no longer a Spectator!", player, ServerMessageType.GAME);
        return true;
    }

}
