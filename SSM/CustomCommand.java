package SSM;

import org.bukkit.entity.Player;

public abstract class CustomCommand {

    public String name = "";

    public abstract void activate(Player player, String[] args);
}
