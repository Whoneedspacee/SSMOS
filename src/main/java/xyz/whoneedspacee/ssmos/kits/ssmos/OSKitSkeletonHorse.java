package xyz.whoneedspacee.ssmos.kits.ssmos;

import org.bukkit.Material;
import xyz.whoneedspacee.ssmos.kits.original.KitSkeletonHorse;
import xyz.whoneedspacee.ssmos.abilities.original.BoneRush;

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
