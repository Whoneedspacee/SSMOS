package ssm.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ssm.abilities.original.Needler;
import ssm.abilities.original.SpinWeb;
import ssm.attributes.*;
import ssm.attributes.doublejumps.custom.SpiderJump;
import ssm.kits.Kit;
import ssm.kits.original.KitSpider;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SpiderDisguise;

public class OSKitSpider extends KitSpider {

    public OSKitSpider() {
        super();
    }

    @Override
    public void initializeKit() {
        super.initializeKit();
        SpiderJump jump = getAttributeByClass(SpiderJump.class);
        if(jump != null) {
            jump.energy_to_jump = 0.1f;
            jump.expUsed = 0.1f;
        }
    }

}
