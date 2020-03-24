package SSM.Kits;

import SSM.Abilities.InkShotgun;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import SSM.Ability;

public class KitSquid extends Kit {


    public KitSquid(Plugin plugin) {
        super(plugin);

        this.damage = 6.0;
        this.speed = 0.24f;
        this.regeneration = 0.25;
        this.knockback = 0;

        this.name = "SkySquid";

        this.armor = new ItemStack[]{
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            null
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.IRON_SWORD),
            new ItemStack(Material.IRON_SHOVEL)
        };

        this.abilities = new Ability[]{
            new InkShotgun(plugin),
            null, //Super Squid
            null //Fish Flurry


        };
    }
}
