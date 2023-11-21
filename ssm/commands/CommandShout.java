package ssm.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ssm.kits.Kit;
import ssm.managers.GameManager;
import ssm.managers.KitManager;
import ssm.managers.gamemodes.TestingGamemode;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

import java.util.HashMap;

public class CommandShout implements CommandExecutor {

    public static HashMap<Player, Long> last_shout_time = new HashMap<Player, Long>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        if(!commandSender.isOp()) {
            Player player = (Player) commandSender;
            last_shout_time.putIfAbsent(player, 0L);
            long time_elapsed = System.currentTimeMillis() - last_shout_time.get(player);
            long cooldown_time = 10000 - time_elapsed;
            if (cooldown_time > 0) {
                Utils.sendServerMessageToPlayer(String.format("You must wait %.1f seconds before using that command again.",
                        cooldown_time / 1000.0), player, ServerMessageType.COMMAND);
                return true;
            }
            last_shout_time.put(player, System.currentTimeMillis());
        }
        StringBuilder message_builder = new StringBuilder();
        message_builder.append(ChatColor.GOLD);
        message_builder.append("SHOUT> ");
        message_builder.append(commandSender.getName());
        message_builder.append(ChatColor.WHITE);
        message_builder.append(" ");
        for (int i = 0; i < args.length; i++) {
            message_builder.append(args[i]);
            message_builder.append(" ");
        }
        String shout_message = message_builder.substring(0, message_builder.length() - 1);
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(shout_message);
        }
        Bukkit.getConsoleSender().sendMessage(shout_message);
        return true;
    }
}
