package SSM.Kits;

import SSM.Abilities.Needler;
import SSM.Abilities.SpinWeb;
import SSM.Attributes.Climb;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitSpider extends Kit {

    public KitSpider() {
        super();
        this.damage = 6;
        this.armor = 5.5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Spider";
        this.hasDirectDoubleJump = true;
        this.menuItem = Material.SPIDER_EYE;
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_SWORD, 0, new Needler());
        setItem(Material.IRON_AXE, 1, new SpinWeb());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new ExpCharge(0.005f, 1, true));
        addAttribute(new Climb(0.2));
        addAttribute(new DirectDoubleJump(1.0, 1.0, 1, Sound.SPIDER_IDLE));
    }

}
