package ssm.commands;

import ssm.gamemanagers.GameManager;
import ssm.gamemanagers.gamemodes.TestingGamemode;
import ssm.gamemanagers.KitManager;
import ssm.kits.Kit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRandomKit implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!(commandSender instanceof Player)) {
            return true;
        }
        if(!(GameManager.getCurrentGamemode() instanceof TestingGamemode)) {
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
