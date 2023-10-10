package SSM.Attributes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Compass extends Attribute {

    public Compass() {
        super();
        this.name = "Compass";
        task = this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        Location finalLoc = null;
        Location loc = owner.getLocation();
        double finalDist = 101;
        Collection<? extends Player> nearby = Bukkit.getOnlinePlayers();
        nearby.remove(owner);
        for (Entity ent : nearby) {
            if (!(ent instanceof Player)) {
                continue;
            }
            Player target = (Player) ent;
            double dist = loc.distance(target.getLocation());
            if (dist < finalDist) {
                finalDist = dist;
                finalLoc = target.getLocation();
            }
        }
        owner.setCompassTarget(finalLoc);
    }

}
