package SSM.Attributes;

import SSM.Attribute;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SquidWatcher;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

;import java.util.List;

public class Compass extends Attribute {

    public Compass() {
        super();
        this.name = "Compass";
        task = this.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void run() {
        Location finalLoc = null;
        Location loc = owner.getLocation();
        double finalDist = 101;
        List<Entity> nearby = owner.getNearbyEntities(100, 100, 100);
        nearby.remove(owner);
        for (Entity ent : nearby){
            if (!(ent instanceof Player)){
                continue;
            }
            Player target = (Player)ent;
            double dist = loc.distance(target.getLocation());
            if (dist < finalDist){
                finalDist = dist;
                finalLoc = target.getLocation();
            }
        }
        owner.setCompassTarget(finalLoc);

    }
}
