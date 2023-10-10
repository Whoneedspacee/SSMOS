package SSM.Kits;

import SSM.Abilities.FlameDash;
import SSM.Abilities.MagmaBlast;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitMagmaCube extends Kit {

    public KitMagmaCube() {
        super();
        this.damage = 5;
        this.armor = 4;
        this.regeneration = 0.35;
        this.knockback = 1.75;
        this.name = "Magma Cube";
        this.menuItem = Material.FIREWORK_CHARGE;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new MagmaBlast());
        setItem(Material.IRON_SPADE, 1, new FlameDash());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1.2, 1.0, 1, Sound.GHAST_FIREBALL));
    }
}
