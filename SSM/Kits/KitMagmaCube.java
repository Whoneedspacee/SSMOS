package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitMagmaCube extends Kit {

    public KitMagmaCube() {
        super();

        this.damage = 5;
        this.speed = 0.22f;
        this.regeneration = 0.3;
        this.knockback = 0;

        this.name = "MagmaCube";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new MagmaBlast());
        setItem(Material.IRON_SHOVEL, 1, new FlameDash());
    }
}
