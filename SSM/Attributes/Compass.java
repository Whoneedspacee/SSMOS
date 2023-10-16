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
        for (Player target : owner.getWorld().getPlayers()) {
            if (target.equals(owner)) {
                continue;
            }
            double dist = loc.distance(target.getLocation());
            if (dist < finalDist) {
                finalDist = dist;
                finalLoc = target.getLocation();
            }
        }
        if(finalLoc == null) {
            return;
        }
        owner.setCompassTarget(finalLoc);
    }

}
