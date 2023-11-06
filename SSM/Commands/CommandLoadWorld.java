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
        StringBuilder path_builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            path_builder.append(args[i]);
            path_builder.append(" ");
        }
        String path_string = path_builder.substring(0, path_builder.length() - 1);
        loadWorld(path_string);
        commandSender.sendMessage("World Loaded!");
        return true;
    }

    public static World loadWorld(String world) {
        WorldCreator worldCreator = new WorldCreator(world);
        World ourWorld = worldCreator.createWorld();
        return ourWorld;
    }

}
