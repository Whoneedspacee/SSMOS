package ssm.commands;

import ssm.gamemanagers.GameManager;
import ssm.gamemanagers.KitManager;
import ssm.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSetPlaying implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        Kit kit = KitManager.getPlayerKit(player);
        if(kit != null) {
            // Need first part to unhandle existing stuff
            kit.updatePlaying(GameManager.GameState.LOBBY_WAITING);
            kit.updatePlaying(GameManager.GameState.GAME_PLAYING);
            player.sendMessage(ChatColor.YELLOW + "Forced Kit State to Playing");
        }
        return true;
    }

}
