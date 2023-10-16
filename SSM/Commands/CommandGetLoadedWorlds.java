package SSM.Commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGetLoadedWorlds implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        commandSender.sendMessage("World List:");
        for(World world : Bukkit.getWorlds()) {
            commandSender.sendMessage(world.getName());
        }
        return true;
    }

}
