package xyz.whoneedspacee.ssmos.commands;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CommandJump implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        Player player = (Player) commandSender;
        CraftPlayer craftplayer = (CraftPlayer) player;
        float power = 1;
        if (args.length == 1) {
            power = Float.parseFloat(args[0]);
        }
        for(Entity entity : player.getWorld().getEntities()) {
            entity.setVelocity(new Vector(0, power, 0));
        }
        //craftplayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), 0, power, 0));
        return true;
    }

}
