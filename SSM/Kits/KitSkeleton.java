package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.ClearProjectile;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class KitSkeleton extends Kit {

    public KitSkeleton() {
        super();

        this.damage = 5;
        this.speed = 0.21f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "Skeleton";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new BoneExplosion());
        setItem(Material.BOW, 1, new RopedArrow());

        addAttribute(new ItemGenerator(Material.ARROW, 1, 3, 3));
        addAttribute(new ClearProjectile());
        addAttribute(new Regeneration(regeneration, 1));
    }

}
