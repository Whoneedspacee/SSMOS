package SSM.Commands;

import SSM.GameManagers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            Bukkit.shutdown();
            return true;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        stopGame();
        return true;
    }

    public static void stopGame() {
        if (GameManager.getState() >= GameManager.GameState.GAME_STARTING) {
            GameManager.setTimeLeft(0);
            GameManager.setState(GameManager.GameState.GAME_ENDING);
        }
        else {
            GameManager.setState(GameManager.GameState.LOBBY_WAITING);
        }
    }

}
