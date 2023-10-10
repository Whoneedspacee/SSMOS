package SSM.Kits;

import SSM.Abilities.FishFlurry;
import SSM.Abilities.InkShotgun;
import SSM.Abilities.SuperSquid;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;

public class KitSquid extends Kit {

    public KitSquid() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Sky_Squid";
        this.menuItem = Material.INK_SACK;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_AXE, 0, new InkShotgun());
        setItem(Material.IRON_SWORD, 1, new SuperSquid());
        setItem(Material.IRON_SPADE, 2, new FishFlurry());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
    }
}
