package ssm.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.managers.GameManager;
import ssm.managers.KitManager;
import ssm.managers.gamemodes.TestingGamemode;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;

public class CommandHub implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        return true;
    }
}
