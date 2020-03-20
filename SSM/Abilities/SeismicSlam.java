package SSM.Abilities;

import SSM.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SeismicSlam extends Ability {

    public SeismicSlam(Plugin plugin) {
        super(plugin);
        this.name = "Seismic Slam";
        this.cooldownTime = 12;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        Double x = player.getLocation().getDirection().getX() * 0.9;
        Double z = player.getLocation().getDirection().getZ() * 0.9;
        player.setVelocity(new Vector(x, 0.42, z));
    }

}
