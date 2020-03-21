package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitIronGolem extends Kit {

    public KitIronGolem(Plugin plugin) {
        super(plugin);

        this.name = "IronGolem";

        this.armor = new ItemStack[]{
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_HELMET)
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_PICKAXE),
            new ItemStack(Material.IRON_SHOVEL)
        };

        this.abilities = new Ability[]{
            null, // fissure
            new IronHook(plugin),
            new SeismicSlam(plugin)
        };
    }

}
