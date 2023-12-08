package ssm.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ssm.abilities.original.Blink;
import ssm.abilities.original.BlockToss;
import ssm.attributes.Compass;
import ssm.attributes.Hunger;
import ssm.attributes.Regeneration;
import ssm.attributes.Teleport;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.kits.original.KitEnderman;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.EndermanDisguise;

public class OSKitEnderman extends KitEnderman {

    public OSKitEnderman() {
        super();
        this.damage = 6.5;
    }

}
