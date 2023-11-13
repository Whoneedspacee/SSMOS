package SSM.Attributes;

import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.CreeperDisguise;
import SSM.GameManagers.Disguises.Disguise;
import SSM.GameManagers.Disguises.SheepDisguise;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntitySheep;
import org.bukkit.entity.Sheep;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class RainbowSheep extends Attribute {

    public RainbowSheep() {
        super();
        this.name = "Rainbow Sheep";
        task = this.runTaskTimer(plugin, 0, 10);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof SheepDisguise)) {
            return;
        }
        SheepDisguise sheepDisguise = (SheepDisguise) disguise;
        EntityLiving living = sheepDisguise.getLiving();
        living.setCustomName("jeb_");
        living.setCustomNameVisible(false);
        //sheepDisguise.setColor((sheepDisguise.getColor() + 1) % 16);
    }

}
