package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();

        this.damage = 7;
        this.speed = 0.18f;
        this.regeneration = 0.2;
        this.knockback = 0;

        this.name = "Iron_Golem";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.DIAMOND_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0);
        setItem(Material.IRON_PICKAXE, 1, new IronHook());
        setItem(Material.IRON_SHOVEL, 2, new SeismicSlam());

        addAttribute(new Regeneration(regeneration, 1));
    }

}
