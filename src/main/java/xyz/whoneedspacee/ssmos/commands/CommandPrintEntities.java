package xyz.whoneedspacee.ssmos.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class CommandPrintEntities implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        for(Entity entity : player.getWorld().getEntities()) {
            player.sendMessage(entity.getName());
        }
        return true;
    }

}
