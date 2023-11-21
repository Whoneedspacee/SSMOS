package ssm.commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.managers.GameManager;
import ssm.managers.gamemodes.SoloGamemode;
import ssm.managers.smashserver.SmashServer;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

public class CommandMakeServer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        SmashServer server = GameManager.createSmashServer(new SoloGamemode());
        commandSender.sendMessage(ServerMessageType.ADMIN + " Created Server: " + server.toString());
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
        }
        return true;
    }

}
