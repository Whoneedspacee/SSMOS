package SSM.Commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandGameMode implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) return false;
        if (!commandSender.isOp()) return true;

        Player player = (Player) commandSender;

        if (args.length != 1) {
            player.sendMessage("invalid arguments");
        }

        if (args.length == 1) {
            String gameMode = args[0];
            switch (gameMode) {
                case "s", "0" -> player.setGameMode(GameMode.SURVIVAL);
                case "c", "1" -> player.setGameMode(GameMode.CREATIVE);
                case "a", "2" -> player.setGameMode(GameMode.ADVENTURE);
                case "sp", "3" -> player.setGameMode(GameMode.SPECTATOR);
                default -> player.sendMessage("invalid argument!");
            }
        }
        return false;
    }
}
