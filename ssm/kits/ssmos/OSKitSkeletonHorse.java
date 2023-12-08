package ssm.kits.ssmos;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import ssm.abilities.original.BoneKick;
import ssm.abilities.original.BoneRush;
import ssm.attributes.Compass;
import ssm.attributes.DeadlyBones;
import ssm.attributes.Hunger;
import ssm.attributes.Regeneration;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.kits.original.KitSkeletonHorse;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SkeletonHorseDisguise;

public class OSKitSkeletonHorse extends KitSkeletonHorse {

    public OSKitSkeletonHorse() {
        super();
        this.armor = 6;
    }

    @Override
    public void initializeKit() {
        super.initializeKit();
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        BoneRush rush = getAttributeByClass(BoneRush.class);
        if(rush != null) {
            rush.duration_ms = 1200;
        }
    }

}
