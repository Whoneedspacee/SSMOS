package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitSheep extends Kit {

    public KitSheep() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.7;
        this.name = "Sheep";
        this.menuItem = Material.WOOL;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_SWORD, 0, null);
        setItem(Material.IRON_AXE, 1, null);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1, 1, 1, Sound.GHAST_FIREBALL));
    }

}
