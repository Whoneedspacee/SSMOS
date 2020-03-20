package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitCreeper extends Kit {

    public KitCreeper() {
        super();

        this.name = "Creeper";

        this.armor = new ItemStack[]{
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.LEATHER_HELMET)
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_SHOVEL)
        };

        this.abilities = new Ability[]{
            new SulphurBomb(),
            null // explode
        };
    }

}
