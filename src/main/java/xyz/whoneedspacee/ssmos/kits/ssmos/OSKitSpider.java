package xyz.whoneedspacee.ssmos.kits.ssmos;

import xyz.whoneedspacee.ssmos.kits.original.KitSpider;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.custom.SpiderJump;

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
