package xyz.whoneedspacee.ssmos.commands;

import xyz.whoneedspacee.ssmos.managers.gamemodes.TestingGamemode;
import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;

public class CommandKit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return true;
        }
        if(!(server.getCurrentGamemode() instanceof TestingGamemode)) {
            if(server.getState() >= GameState.GAME_PLAYING && !commandSender.isOp()) {
                commandSender.sendMessage("You may not use this command while a game is in progress.");
                return true;
            }
        }
        KitManager.openKitMenu(player);
        return true;
    }
}
