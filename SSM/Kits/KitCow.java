package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.Attributes.Stampede;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitCow extends Kit {

    public KitCow() {
        super();
        this.damage = 6;
        this.armor = 7;
        this.speed = 0.20f;
        this.regeneration = 0.25;
        this.knockback = 1.1;
        this.disguise = DisguiseType.COW;
        this.name = "Cow";
        this.menuItem = Material.MILK_BUCKET;
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0);
        setItem(Material.IRON_SHOVEL, 1);

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new Stampede(3, 3));
    }
}
