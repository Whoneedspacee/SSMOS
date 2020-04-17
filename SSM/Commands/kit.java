package SSM.Commands;

import SSM.CustomCommand;
import SSM.Kit;
import org.bukkit.entity.Player;

import static SSM.SSM.allKits;
import static SSM.SSM.equipPlayer;

public class kit extends CustomCommand {

    public kit(){
        this.name = "kit";
    }

    public void activate(Player player, String[] args){
        boolean validKit = false;
        if (args.length == 1) {
            for (Kit check : allKits) {
                if (check.getName().equalsIgnoreCase(args[0])) {
                    equipPlayer(player, check);
                    validKit = true;
                    break;
                }
            }
        }
        if (!validKit){
            String finalMessage = "Kit Choices: ";
            for (Kit kit : allKits) {
                finalMessage += kit.getName() + ", ";
            }
            player.sendMessage(finalMessage.replace("_", " "));
        }

    }
}
