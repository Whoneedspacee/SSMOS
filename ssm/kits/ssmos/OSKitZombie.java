package ssm.kits.ssmos;

import ssm.attributes.*;
import ssm.kits.original.KitZombie;

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
