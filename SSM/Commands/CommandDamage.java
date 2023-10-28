package SSM.Commands;

import SSM.Utilities.DamageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class CommandDamage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        if (args.length == 1) {
            try {
                int number = Integer.parseInt(args[0]);
                DamageUtil.damage(player, null, number, 0, true,
                        EntityDamageEvent.DamageCause.CUSTOM, null, "Command");
                player.sendMessage("You were dealt " + number + " damage");
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("You need to input a number!");
                return true;
            }
        }
        return false;
    }

}
