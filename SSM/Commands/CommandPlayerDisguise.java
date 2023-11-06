package SSM.Commands;

import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.PlayerDisguise;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlayerDisguise implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (!(commandSender instanceof Player)) {
            return true;
        }
        if(args.length < 1) {
            Bukkit.broadcastMessage("Specify the name of the player to turn into.");
            return true;
        }
        Player player = (Player) commandSender;
        DisguiseManager.addDisguise(player, new PlayerDisguise(player));
        return true;
    }

}
