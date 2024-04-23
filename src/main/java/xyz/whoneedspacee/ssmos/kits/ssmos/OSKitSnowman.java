package xyz.whoneedspacee.ssmos.kits.ssmos;

import xyz.whoneedspacee.ssmos.attributes.ExpCharge;
import xyz.whoneedspacee.ssmos.kits.original.KitSnowMan;
import xyz.whoneedspacee.ssmos.abilities.original.Blizzard;

public class OSKitSnowman extends KitSnowMan {

    public OSKitSnowman() {
        super();
    }

    @Override
    public void initializeKit() {
        super.initializeKit();
        ExpCharge charge = getAttributeByClass(ExpCharge.class);
        if(charge != null) {
            charge.expAdd = 0.005f;
        }
        Blizzard blizzard = getAttributeByClass(Blizzard.class);
        if(blizzard != null) {
            blizzard.energy_per_shot = 0.2f;
            blizzard.minimum_energy_required = 0.2f;
            blizzard.ticks_to_fire = 1;
        }
    }

}
