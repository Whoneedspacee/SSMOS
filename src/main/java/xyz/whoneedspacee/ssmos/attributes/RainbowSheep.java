package xyz.whoneedspacee.ssmos.attributes;

import xyz.whoneedspacee.ssmos.managers.disguises.Disguise;
import xyz.whoneedspacee.ssmos.managers.disguises.SheepDisguise;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import net.minecraft.server.v1_8_R3.EntityLiving;

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
