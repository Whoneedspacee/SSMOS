package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitWitch extends Kit {

    public KitWitch(Plugin plugin) {
        super(plugin);

        this.damage = 6.0;
        this.speed = 0.21f;
        this.regeneration = 0.3;
        this.knockback = 0;

        this.name = "Witch";

        this.armor = new ItemStack[]{
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            null
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_SHOVEL)
        };

        this.abilities = new Ability[]{
            null, // Daze Potion
            null, // Bat Wave
        };
    }

}
