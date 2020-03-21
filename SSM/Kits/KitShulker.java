package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitShulker extends Kit {

    public KitShulker(Plugin plugin) {
        super(plugin);

        this.damage = 4.5;
        this.speed = 0.15f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "Shulker";

        this.armor = new ItemStack[]{
                new ItemStack(Material.IRON_BOOTS),
                new ItemStack(Material.IRON_LEGGINGS),
                new ItemStack(Material.IRON_CHESTPLATE),
                new ItemStack(Material.IRON_HELMET)
        };

        this.weapons = new ItemStack[]{
                new ItemStack(Material.IRON_AXE),
        };

        this.abilities = new Ability[]{
                new Shulker(plugin),
        };


    }

}
