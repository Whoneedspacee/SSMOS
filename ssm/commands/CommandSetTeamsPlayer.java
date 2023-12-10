package ssm.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ssm.managers.GameManager;
import ssm.managers.gamemodes.TeamsGamemode;
import ssm.managers.smashserver.SmashServer;

public class CommandSetTeamsPlayer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if(!commandSender.isOp()) {
            return true;
        }
        if(args.length <= 0) {
            commandSender.sendMessage("You must specify a player count");
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server != null && server.getCurrentGamemode() instanceof TeamsGamemode) {
            TeamsGamemode gamemode = (TeamsGamemode) server.getCurrentGamemode();
            gamemode.players_per_team = Integer.parseInt(args[0]);
            commandSender.sendMessage("Set players per team to: " + gamemode.players_per_team);
        }
        return true;
    }

}
