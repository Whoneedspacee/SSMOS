package SSM.Commands;

import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class CommandMove implements CommandExecutor {

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
        craftplayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityVelocity(player.getEntityId(), power, 0, 0));
        return true;
    }

}
