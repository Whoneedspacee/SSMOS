package SSM.Kits;

import SSM.Abilities.SelectKit;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class KitChoose extends Kit {

    public KitChoose() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.speed = 0.21f;
        this.regeneration = 0.4;
        this.knockback = 0;
        this.name = "Choose";
        this.disguise = DisguiseType.WANDERING_TRADER;
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.CLOCK, 0, new SelectKit());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.9, 1));
    }
}
