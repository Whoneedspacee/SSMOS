package SSM.Kits;

import SSM.Abilities.Blizzard;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSnowMan extends Kit {

    public KitSnowMan() {
        super();

        this.damage = 7;
        this.armor = 6;
        this.speed = 0.23f;
        this.regeneration = 0.2;
        this.knockbackTaken = 0;
        this.disguise = DisguiseType.SNOWMAN;
        this.name = "Snowman";
        this.menuItem = new ItemStack(Material.SNOWBALL);
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
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1));
        addAttribute(new ExpCharge(0.01F,1));
    }
}
