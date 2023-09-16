package SSM.Kits;

import SSM.Abilities.BouncyBacon;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitPig extends Kit {

    public KitPig() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.speed = 0.20f;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Pig";
        this.menuItem = Material.PORK;
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_AXE, 0, new BouncyBacon());
        setItem(Material.IRON_SPADE, 1);

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.011F, 1, false));
    }
}
