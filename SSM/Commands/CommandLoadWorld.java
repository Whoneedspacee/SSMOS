package SSM.Commands;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLoadWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (args.length < 1) {
            commandSender.sendMessage("Must specify a world to load.");
            return true;
        }
        loadWorld(args[0]);
        commandSender.sendMessage("World Loaded!");
        return true;
    }

    public static World loadWorld(String world) {
        WorldCreator worldCreator = new WorldCreator(world);
        World ourWorld = worldCreator.createWorld();
        return ourWorld;
    }

}
