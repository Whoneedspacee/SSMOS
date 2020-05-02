package SSM.Commands;

import SSM.CustomCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class rank extends CustomCommand {

    private HashMap<String, ChatColor> ranks = new HashMap<>();

    public rank(){
        this.name = "rank";
        ranks.put("ETERNAL", ChatColor.DARK_AQUA);
        ranks.put("IMMORTAL", ChatColor.YELLOW);
        ranks.put("TITAN", ChatColor.RED);
        ranks.put("LEGEND", ChatColor.GREEN);
        ranks.put("HERO", ChatColor.LIGHT_PURPLE);
        ranks.put("ULTRA", ChatColor.AQUA);
        ranks.put("DEVELOPER", ChatColor.DARK_RED);

    }

    public void activate(Player player, String[] args){
        for (String rank : ranks.keySet()){
            if (args[0].equalsIgnoreCase(rank)){
                player.setDisplayName(""+ChatColor.BOLD+""+ranks.get(rank)+ ChatColor.BOLD +rank.toUpperCase()+""+ChatColor.RESET+ChatColor.YELLOW+" "+player.getName()+ChatColor.RESET);

            }
        }
    }
}
