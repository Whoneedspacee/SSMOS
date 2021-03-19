package SSM.Kits;

import SSM.Abilities.Blizzard;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitSnowMan extends Kit {

    public KitSnowMan() {
        super();
        this.damage = 7;
        this.armor = 6;
        this.speed = 0.23f;
        this.regeneration = 0.2;
        this.knockback = 1.4;
        this.name = "Snowman";
        this.menuItem = Material.PUMPKIN;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0, new Blizzard());
        setItem(Material.IRON_AXE, 1);

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new ExpCharge(0.01F, 1, false));
    }
}
