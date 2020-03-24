package SSM.Kits;

import SSM.Abilities.Blizzard;
import SSM.Abilities.IceBridge;
import SSM.Ability;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class KitSnowMan extends Kit {

    public KitSnowMan(Plugin plugin) {
        super(plugin);

        this.damage = 7.0;
        this.speed = 0.23f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "SnowMan";

        this.armor = new ItemStack[]{
                new ItemStack(Material.CHAINMAIL_BOOTS),
                new ItemStack(Material.CHAINMAIL_LEGGINGS),
                new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                new ItemStack(Material.CHAINMAIL_HELMET)
        };

        this.weapons = new ItemStack[]{
                new ItemStack(Material.IRON_SWORD),
                new ItemStack(Material.IRON_AXE),
        };

        this.abilities = new Ability[]{
                new Blizzard(plugin),
                new IceBridge(plugin)
        };
    }
}
