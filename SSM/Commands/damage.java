package SSM.Commands;

import SSM.CustomCommand;
import SSM.Kit;
import org.bukkit.entity.Player;

import static SSM.SSM.playerKit;

public class damage extends CustomCommand {

    public damage(){
        this.name = "damage";
    }

    public void activate(Player player, String[] args){
        player.damage(Double.parseDouble(args[0]));
    }
}
