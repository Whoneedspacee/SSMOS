package SSM.Commands;

import SSM.GameManagers.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStart implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (GameManager.getState() <= GameManager.GameState.LOBBY_STARTING) {
            GameManager.setTimeLeft(0);
            GameManager.ourInstance.run();
        }
        return true;
    }

}
