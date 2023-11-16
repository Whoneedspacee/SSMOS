package ssm.commands;

import ssm.gamemanagers.GameManager;
import ssm.gamemanagers.maps.MapFile;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CommandLoadGamemodeWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        List<MapFile> worlds = GameManager.getCurrentGamemode().getAllowedMaps();
        if (args.length < 1) {
            StringBuilder world_string = new StringBuilder("Options: ");
            for(MapFile mapFile : worlds) {
                world_string.append(mapFile.getName()).append(", ");
            }
            commandSender.sendMessage(world_string.substring(0, world_string.length() - 2));
            return true;
        }
        StringBuilder name_builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            name_builder.append(args[i]);
            name_builder.append(" ");
        }
        String name_string = name_builder.substring(0, name_builder.length() - 1);
        for(MapFile mapFile : worlds) {
            if(mapFile.getName().equalsIgnoreCase(name_string)) {
                loadWorld(mapFile.getMapDirectory().getPath());
                commandSender.sendMessage("World Loaded!");
                break;
            }
        }
        return true;
    }

    public static World loadWorld(String world) {
        WorldCreator worldCreator = new WorldCreator(world);
        World ourWorld = worldCreator.createWorld();
        return ourWorld;
    }

}
