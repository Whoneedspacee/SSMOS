package SSM.Attributes;

import SSM.Attribute;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SquidWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

;import java.util.List;

public class SquidFix extends Attribute {

    public SquidFix() {
        super();
        this.name = "Regeneration";
        task = this.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        List<Entity> canHit = owner.getNearbyEntities(10, 10, 10);
        canHit.remove(owner);

        for (Entity entity : canHit) {
            switch ((entity.getType())) {
                case SQUID:
                    entity.setVelocity(new Vector(0, 0, 0));
            }
        }
    }
}
