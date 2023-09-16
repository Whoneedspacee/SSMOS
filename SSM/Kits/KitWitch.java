package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitWitch extends Kit {

    public KitWitch() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.speed = 0.21f;
        this.regeneration = 0.3;
        this.knockback = 1.5;
        this.name = "Witch";
        this.menuItem = Material.POTION;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_AXE, 0);
        setItem(Material.IRON_SPADE, 1);

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.GHAST_FIREBALL));
    }

}
