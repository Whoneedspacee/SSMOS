package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitSlime extends Kit {

    public KitSlime() {
        super();

        this.damage = 6;
        this.speed = 0.21f;
        this.regeneration = 0.35;
        this.knockback = 0;

        this.name = "Slime";
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0);
        setItem(Material.IRON_AXE, 1);

        addAttribute(new Regeneration(regeneration, 1));
    }

}
