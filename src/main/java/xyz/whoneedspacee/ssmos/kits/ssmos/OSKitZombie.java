package xyz.whoneedspacee.ssmos.kits.ssmos;

import xyz.whoneedspacee.ssmos.attributes.ArrowDamageMultiplier;
import xyz.whoneedspacee.ssmos.kits.original.KitZombie;

public class OSKitZombie extends KitZombie {

    public OSKitZombie() {
        super();
    }

    @Override
    public void initializeKit() {
        super.initializeKit();
        addAttribute(new ArrowDamageMultiplier(0.85));
    }

}
