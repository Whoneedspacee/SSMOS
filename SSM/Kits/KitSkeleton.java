package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class KitSkeleton extends Kit {

    public KitSkeleton(Plugin plugin) {
        super(plugin);

        this.damage = 5.0;
        this.speed = 0.21f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "Skeleton";

        this.armor = new ItemStack[]{
            new ItemStack(Material.CHAINMAIL_BOOTS),
            new ItemStack(Material.CHAINMAIL_LEGGINGS),
            new ItemStack(Material.CHAINMAIL_CHESTPLATE),
            new ItemStack(Material.CHAINMAIL_HELMET)
        };

        this.weapons = new ItemStack[]{
            new ItemStack(Material.IRON_AXE),
            new ItemStack(Material.BOW)
        };

        this.abilities = new Ability[]{
            new BoneExplosion(plugin),
            new RopedArrow(plugin)
        };
    }

}
