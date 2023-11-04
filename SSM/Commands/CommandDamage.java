package SSM.Commands;

import SSM.Events.SmashDamageEvent;
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
        if (args.length >= 1) {
            try {
                int damage_number = Integer.parseInt(args[0]);
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, null, damage_number);
                smashDamageEvent.multiplyKnockback(0);
                //smashDamageEvent.setIgnoreArmor(true);
                smashDamageEvent.setDamageCause(EntityDamageEvent.DamageCause.STARVATION);
                smashDamageEvent.setDamagerName("Command");
                smashDamageEvent.setReason("Command");
                smashDamageEvent.callEvent();
                player.sendMessage("You were dealt " + damage_number + " damage");
                if(args.length >= 2) {
                    int hunger_number = Integer.parseInt(args[1]);
                    player.setFoodLevel(hunger_number);
                    player.sendMessage("Your hunger was set to " + hunger_number);
                }
                return true;
            } catch (NumberFormatException e) {
                player.sendMessage("You need to input a number!");
                return true;
            }
        }
        return false;
    }

}
