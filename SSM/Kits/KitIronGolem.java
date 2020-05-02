package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();

        this.damage = 7;
        this.armor = 8;
        this.speed = 0.18f;
        this.regeneration = 0.2;
        this.knockbackTaken = 1.00;
        this.disguise = DisguiseType.IRON_GOLEM;
        this.name = "Iron_Golem";
        this.menuItem = new ItemStack(Material.IRON_BLOCK);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.DIAMOND_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new Fissure());
        setItem(Material.IRON_PICKAXE, 1, new IronHook());
        setItem(Material.IRON_SHOVEL, 2, new SeismicSlam());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1));
    }

}
