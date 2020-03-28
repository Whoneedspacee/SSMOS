package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitShulker extends Kit {

    public KitShulker() {
        super();

        this.damage = 5;
        this.speed = 0.15f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "Shulker";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new Shulker());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1));

    }
}
