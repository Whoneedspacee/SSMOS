package SSM.Commands;

import SSM.GameManagers.GameManager;
import SSM.GameManagers.Gamemodes.TestingGamemode;
import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandRandomKit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        if(!(GameManager.getGamemode() instanceof TestingGamemode)) {
            if(GameManager.getState() >= GameManager.GameState.GAME_PLAYING && !commandSender.isOp()) {
                commandSender.sendMessage("You may not use this command while a game is in progress.");
                return true;
            }
        }
        Player player = (Player) commandSender;
        Kit random = KitManager.getAllKits().get((int) (Math.random() * KitManager.getAllKits().size()));
        KitManager.equipPlayer(player, random);
        commandSender.sendMessage("Equipped: " + random.getName());
        return true;
    }
}
