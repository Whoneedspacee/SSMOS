package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitSkeletonHorse extends Kit {

    public KitSkeletonHorse() {
        super();
        this.damage = 6;
        this.armor = 6.5;
        this.regeneration = 0.3;
        this.knockback = 1.25;
        this.name = "Skeleton Horse";
        this.menuItem = Material.SADDLE;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_AXE, 0, null);
        setItem(Material.IRON_SPADE, 1, null);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));
    }

}
