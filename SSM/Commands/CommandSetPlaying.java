package SSM.Commands;

import SSM.GameManagers.GameManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.Kit;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandSetPlaying implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if(!commandSender.isOp()) {
            return true;
        }
        if (!(commandSender instanceof Player)) {
            return true;
        }
        Player player = (Player) commandSender;
        Kit kit = KitManager.getPlayerKit(player);
        if(kit != null) {
            // Need first part to unhandle existing stuff
            kit.updatePlaying(GameManager.GameState.LOBBY_WAITING);
            kit.updatePlaying(GameManager.GameState.GAME_PLAYING);
            player.sendMessage(ChatColor.YELLOW + "Forced Kit State to Playing");
        }
        return true;
    }

}
