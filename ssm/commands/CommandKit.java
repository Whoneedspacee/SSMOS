package ssm.commands;

import ssm.managers.GameManager;
import ssm.managers.gamemodes.TestingGamemode;
import ssm.managers.KitManager;
import ssm.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;

public class CommandKit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return true;
        }
        if(!(server.getCurrentGamemode() instanceof TestingGamemode)) {
            if(server.getState() >= GameState.GAME_PLAYING && !commandSender.isOp()) {
                commandSender.sendMessage("You may not use this command while a game is in progress.");
                return true;
            }
        }
        KitManager.openKitMenu(player);
        return true;
    }
}
