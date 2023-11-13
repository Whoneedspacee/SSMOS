package SSM.Commands;

import SSM.GameManagers.DisplayManager;
import SSM.GameManagers.GameManager;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSetLives implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        if (args.length == 2) {
            Player set = Bukkit.getPlayer(args[0]);
            if(set == null) {
                player.sendMessage("Player not found!");
                return true;
            }
            try {
                int number = Integer.parseInt(args[1]);
                GameManager.getAllLives().put(set, number);
                for(Player to_message : set.getWorld().getPlayers()) {
                    Utils.sendServerMessageToPlayer(ChatColor.YELLOW + set.getName() + ChatColor.GRAY +
                            " had their lives set to " + ChatColor.GREEN + number + ChatColor.GRAY + ".", to_message, ServerMessageType.GAME);
                }
                DisplayManager.buildScoreboard();
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("You need to input a number!");
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        List<String> list = new ArrayList<String>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            list.add(p.getName());
        }
        return list;
    }

}
