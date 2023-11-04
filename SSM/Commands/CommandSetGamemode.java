package SSM.Commands;

import SSM.GameManagers.DisplayManager;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.Gamemodes.SmashGamemode;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSetGamemode implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (args.length < 1) {
            commandSender.sendMessage("Must specify a gamemode.");
            return true;
        }
        StringBuilder name_builder = new StringBuilder();
        for(int i = 0; i < args.length; i++) {
            name_builder.append(args[i]);
            name_builder.append(" ");
        }
        String gamemode_name = name_builder.substring(0, name_builder.length() - 1);
        SmashGamemode selected_gamemode = null;
        for(SmashGamemode gamemode : GameManager.all_gamemodes) {
            if(gamemode.getName().equalsIgnoreCase(gamemode_name)) {
                selected_gamemode = gamemode;
                break;
            }
        }
        if(selected_gamemode == null) {
            commandSender.sendMessage("Unable to find specified gamemode.");
            return true;
        }
        GameManager.setGamemode(selected_gamemode);
        DisplayManager.buildScoreboard();
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.sendServerMessageToPlayer("Set " +
                    ChatColor.YELLOW + selected_gamemode.getName() + ChatColor.GRAY +
                    " as the next gamemode.", player, ServerMessageType.GAME);
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
        }
        CommandStop.stopGame();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        StringBuilder name_builder = new StringBuilder();
        for(SmashGamemode gamemode : GameManager.all_gamemodes) {
            name_builder.append(gamemode.getName());
            name_builder.append(", ");
        }
        if(name_builder.length() <= 2) {
            return new ArrayList<String>();
        }
        commandSender.sendMessage(name_builder.substring(0, name_builder.length() - 2));
        return new ArrayList<String>();
    }

}
