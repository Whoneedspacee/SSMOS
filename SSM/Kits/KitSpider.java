package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class KitSpider extends Kit {

    public KitSpider() {
        super();

        this.name = "Spider";

        this.armor = new ItemStack[]{
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            null
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.IRON_AXE)
        };

        this.abilities = new Ability[]{
            null, // needler
            null // spin web
        };
    }

}
