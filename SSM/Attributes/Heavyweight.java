package SSM.Attributes;

import SSM.Attribute;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import me.libraryaddict.disguise.disguisetypes.watchers.SquidWatcher;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

;import java.util.List;

public class Heavyweight extends Attribute {

    public Heavyweight() {
        super();
        this.name = "Heavyweight";
        task = this.runTaskTimer(plugin, 0, 60);
    }

    @Override
    public void run() {
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70 ,0));
    }
}
