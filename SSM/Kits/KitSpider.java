package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.Climb;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.Attributes.Regeneration;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSpider extends Kit {

    public KitSpider() {
        super();

        this.damage = 6;
        this.armor = 5.5;
        this.speed = 0.21f;
        this.regeneration = 0.25;
        this.knockbackTaken = 0;
        this.disguise = DisguiseType.SPIDER;
        this.name = "Spider";
        this.hasDirectDoubleJump = true;
        this.doubleJumpHeight = 0.1;
        this.doubleJumpPower = 1.5;
        this.menuItem = new ItemStack(Material.SPIDER_EYE);
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_SWORD, 0, new Needler());
        setItem(Material.IRON_AXE, 1, new SpinWeb());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new Climb(0.2));
        addAttribute(new DirectDoubleJump(1.0, 0.2, 1));
    }

}
