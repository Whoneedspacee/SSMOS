package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitCreeper extends Kit {

    public KitCreeper(Plugin plugin) {
        super(plugin);

        this.damage = 6.0D;

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
            new SulphurBomb(plugin),
            null // explode
        };


}

}
