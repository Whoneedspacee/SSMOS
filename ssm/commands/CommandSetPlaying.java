package ssm.commands;

import ssm.managers.KitManager;
import ssm.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.managers.gamestate.GameState;

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
            kit.updatePlaying(GameState.GAME_PLAYING, false);
            player.sendMessage(ChatColor.YELLOW + "Forced Kit State to Playing");
        }
        return true;
    }

}
