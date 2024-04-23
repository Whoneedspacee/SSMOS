package xyz.whoneedspacee.ssmos.commands;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandPing implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        player.sendMessage("Player Ping:");
        for(Player check : player.getWorld().getPlayers()) {
            EntityPlayer nms_check = ((CraftPlayer) check).getHandle();
            player.sendMessage(check.getName() + ": " + nms_check.ping);
        }
        return true;
    }

}
