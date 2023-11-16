package ssm.commands;

import ssm.gamemanagers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnloadWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (args.length < 1) {
            if(commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if(unloadWorld(player.getWorld().getName())) {
                    commandSender.sendMessage("World Saved!");
                } else {
                    commandSender.sendMessage("Could not unload world.");
                }
                return true;
            }
            StringBuilder world_string = new StringBuilder("Options: ");
            for(World world : Bukkit.getWorlds()) {
                world_string.append(world.getName()).append(", ");
            }
            commandSender.sendMessage(world_string.substring(0, world_string.length() - 2));
            return true;
        }
        StringBuilder path_builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            path_builder.append(args[i]);
            path_builder.append(" ");
        }
        String path_string = path_builder.substring(0, path_builder.length() - 1);
        if (unloadWorld(path_string)) {
            commandSender.sendMessage("World Saved!");
            return true;
        }
        for(World world : Bukkit.getWorlds()) {
            if(world.getName().contains(path_string)) {
                if (unloadWorld(world.getName())) {
                    commandSender.sendMessage("World Saved!");
                    return true;
                }
            }
        }
        commandSender.sendMessage("Could not find world specified.");
        return true;
    }

    public static boolean unloadWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            return false;
        }
        for (Player player : world.getPlayers()) {
            player.teleport(GameManager.getLobbyWorld().getSpawnLocation());
        }
        Bukkit.unloadWorld(world, true);
        return true;
    }

}
