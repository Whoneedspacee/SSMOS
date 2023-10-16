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
        if(!(commandSender instanceof Player)) {
            Bukkit.shutdown();
            return true;
        }
        if(GameManager.getState() == GameManager.GameState.GAME_STARTING ||
            GameManager.getState() == GameManager.GameState.GAME_PLAYING) {
            GameManager.setState(GameManager.GameState.GAME_ENDING);
            GameManager.setTimeLeft(0);
            GameManager.ourInstance.run();
        }
        return true;
    }

}
