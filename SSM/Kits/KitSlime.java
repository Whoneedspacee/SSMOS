package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitSlime extends Kit {

    public KitSlime(Plugin plugin) {
        super(plugin);

        this.name = "Slime";

        this.armor = new ItemStack[]{
            new ItemStack(Material.CHAINMAIL_BOOTS),
            null,
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_HELMET)
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.IRON_AXE)
        };

        this.abilities = new Ability[]{
            null, // slime rocket
            null // slime slam
        };
    }

}
