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

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 10;
        this.rightClickActivate = true;
    }

    public void activate() {
        Double x = owner.getLocation().getDirection().getX() * 0.9;
        Double z = owner.getLocation().getDirection().getZ() * 0.9;
        owner.setVelocity(new Vector(x, 0.42, z));
    }

}
