package ssm.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ssm.abilities.original.Blizzard;
import ssm.abilities.original.IcePath;
import ssm.abilities.original.Inferno;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.kits.original.KitSnowMan;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SnowmanDisguise;

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
