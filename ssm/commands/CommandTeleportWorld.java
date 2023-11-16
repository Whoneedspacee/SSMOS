package ssm.commands;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeleportWorld implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player to teleport to worlds.");
            return true;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        if (args.length < 1) {
            StringBuilder world_string = new StringBuilder("Options: ");
            for(World world : Bukkit.getWorlds()) {
                world_string.append(world.getName()).append(", ");
            }
            commandSender.sendMessage(world_string.substring(0, world_string.length() - 2));
            return true;
        }
        World previous = player.getWorld();
        teleportToWorld(player, args[0]);
        commandSender.sendMessage("Sending to World: " + player.getWorld().getName());
        return true;
    }

    public void teleportToWorld(Player player, String check) {
        // Check for exact names first
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase(check)) {
                player.teleport(world.getSpawnLocation());
                return;
            }
        }
        // Check for matched names
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().contains(check)) {
                player.teleport(world.getSpawnLocation());
                return;
            }
        }
    }

}
