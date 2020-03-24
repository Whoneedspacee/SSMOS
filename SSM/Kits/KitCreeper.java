package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitCreeper extends Kit {

    public KitCreeper() {
        super();

        this.damage = 6;
        this.speed = 0.21f;
        this.regeneration = 0.4;
        this.knockback = 0;

        this.name = "Creeper";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.LEATHER_LEGGINGS, 1);
        setArmor(Material.LEATHER_CHESTPLATE, 2);
        setArmor(Material.LEATHER_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new SulphurBomb());
        setItem(Material.IRON_SHOVEL, 1);
    }

}
